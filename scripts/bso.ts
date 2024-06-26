import { gunzip } from "https://deno.land/x/compress@v0.4.5/mod.ts";
import { red, yellow, green, cyan, reset } from "jsr:@std/fmt/colors";

class BinaryReader {
  private _data: Uint8Array;
  private _view: DataView;
  private _cursor: number;

  constructor(data: Uint8Array) {
    this._cursor = 0;
    this._view = new DataView(data.buffer);
    this._data = data;
  }

  public readInt8() {
    return this._view.getInt8(this._cursor++);
  }

  public readUint8() {
    return this._view.getUint8(this._cursor++);
  }

  public readInt16() {
    const value = this._view.getInt16(this._cursor);
    this._cursor += 2;
    return value;
  }

  public readUint16() {
    const value = this._view.getUint16(this._cursor);
    this._cursor += 2;
    return value;
  }

  public readInt32() {
    const value = this._view.getInt32(this._cursor);
    this._cursor += 4;
    return value;
  }

  public readUint32() {
    const value = this._view.getUint32(this._cursor);
    this._cursor += 4;
    return value;
  }

  public readInt64() {
    const value = this._view.getBigInt64(this._cursor);
    this._cursor += 8;
    return value;
  }

  public readUint64() {
    const value = this._view.getBigUint64(this._cursor);
    this._cursor += 8;
    return value;
  }

  public readFloat32() {
    const value = this._view.getFloat32(this._cursor);
    this._cursor += 4;
    return value;
  }

  public readFloat64() {
    const value = this._view.getFloat64(this._cursor);
    this._cursor += 8;
    return value;
  }

  public readUtf8() {
    const len = this.readUint16();
    const data = this._data.subarray(this._cursor, this._cursor + len);
    this._cursor += len;
    return new TextDecoder().decode(data);
  }
}

interface BsoStringTag {
  kind: "string";
  value: string;
}

interface BsoNumberTag {
  kind: "byte" | "short" | "int" | "float" | "double";
  value: number;
}

interface BsoLongTag {
  kind: "long";
  value: bigint;
}

interface BsoMapTag {
  kind: "map";
  value: Record<string, BsoTag>;
}

interface BsoListTag {
  kind: "list";
  value: BsoTag[];
}

interface BsoArrayTag {
  kind: "array";
  type: "long" | "string" | "map" | "list" | "array";
  value: BsoTag[];
}

interface BsoArrayNumberTag {
  kind: "array";
  type: "byte" | "short" | "int" | "float" | "double";
  value: number[];
}

interface BsoArrayLongTag {
  kind: "array";
  type: "long";
  value: bigint[];
}

type BsoTag =
  | BsoLongTag
  | BsoNumberTag
  | BsoStringTag
  | BsoMapTag
  | BsoArrayTag
  | BsoListTag
  | BsoArrayNumberTag
  | BsoArrayLongTag;

function read(data: Uint8Array) {
  const reader = new BinaryReader(data);

  let id = reader.readUint8();
  const ad = id >> 4;
  id = id & 0xf;

  function _read(reader: BinaryReader, id: number, ad: number): BsoTag {
    if (id == 0x1) return { kind: "byte", value: reader.readInt8() };
    if (id == 0x2)
      return {
        kind: "short",
        value: ad == 0 ? reader.readInt16() : reader.readInt8(),
      };
    if (id == 0x3)
      return {
        kind: "int",
        value:
          ad == 0
            ? reader.readInt32()
            : ad == 1
            ? reader.readInt16()
            : reader.readInt8(),
      };
    if (id == 0x4)
      return {
        kind: "long",
        value:
          ad == 0
            ? reader.readInt64()
            : ad == 1
            ? BigInt(reader.readInt32())
            : ad == 2
            ? BigInt(reader.readInt16())
            : BigInt(reader.readInt8()),
      };
    if (id == 0x5) return { kind: "float", value: reader.readFloat32() };
    if (id == 0x6) return { kind: "double", value: reader.readFloat64() };
    if (id == 0x7) return { kind: "string", value: reader.readUtf8() };
    if (id == 0x8) {
      const obj: Record<string, BsoTag> = {};

      let byte: number;
      while ((byte = reader.readInt8()) != 0) {
        const bad = byte >> 4;
        const bid = byte & 0xf;
        obj[reader.readUtf8()] = _read(reader, bid, bad);
      }

      return { kind: "map", value: obj };
    }
    if (id == 0x9) {
      const size =
        ad == 0x0
          ? reader.readInt32()
          : ad == 0x1
          ? reader.readUint16()
          : reader.readUint8();
      const list: BsoTag[] = [];

      for (let i = 0; i < size; ++i) {
        const iid = reader.readInt8();
        list.push(_read(reader, iid & 0xf, iid >> 4));
      }

      return { kind: "list", value: list };
    }
    if (id == 0xa) {
      const typeOfList = reader.readInt8() & 0xf;
      const size =
        ad == 0x0
          ? reader.readInt32()
          : ad == 0x1
          ? reader.readUint16()
          : reader.readUint8();

      const list: BsoTag[] = [];
      for (let i = 0; i < size; ++i) {
        if (typeOfList == 0x1) list.push(reader.readInt8() as any);
        else if (typeOfList == 0x2) list.push(reader.readInt16() as any);
        else if (typeOfList == 0x3) list.push(reader.readInt32() as any);
        else if (typeOfList == 0x4) list.push(reader.readInt64() as any);
        else if (typeOfList == 0x5) list.push(reader.readFloat32() as any);
        else if (typeOfList == 0x6) list.push(reader.readFloat64() as any);
        else list.push(_read(reader, typeOfList, 0));
      }

      return {
        kind: "array",
        type: (
          [
            "byte",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "string",
            "map",
            "list",
            "array",
          ] as any
        )[typeOfList],
        value: list,
      };
    }
    return null as unknown as BsoTag;
  }

  return _read(reader, id, ad);
}

function stringify(tag: BsoTag, indent = 0, useAnsi = false) {
  function _stringify(tag: BsoTag, indent: number, level: number) {
    if (tag.kind === "byte")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("b"))
        : tag.value + "b";
    if (tag.kind === "short")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("s"))
        : tag.value + "s";
    if (tag.kind === "int")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("i"))
        : tag.value + "i";
    if (tag.kind === "long")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("L"))
        : tag.value + "L";
    if (tag.kind === "float")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("f"))
        : tag.value + "f";
    if (tag.kind === "double")
      return useAnsi
        ? reset(yellow(tag.value + "") + red("D"))
        : tag.value + "D";
    if (tag.kind === "string")
      return '"' + (useAnsi ? reset(green(tag.value)) : tag.value) + '"';
    if (tag.kind === "map") {
      let res = "{";
      if (indent > 0 && Object.keys(tag.value).length > 0) {
        res += "\n";
      }
      let i = 0;
      const size = Object.keys(tag.value).length;
      for (const entry of Object.entries(tag.value)) {
        if (indent > 0) res += " ".repeat(level * indent + indent);

        res += (useAnsi ? reset(cyan(entry[0])) : entry[0]) + ":";
        if (indent > 0) res += " ";
        res += _stringify(entry[1], indent, level + 1);
        ++i;
        if (i < size) res += ",";
        if (indent > 0) res += "\n";
      }
      if (indent > 0 && size > 0) res += " ".repeat(level * indent);
      res += "}";
      return res;
    }
    if (tag.kind === "list") {
      let res = "[";
      if (indent > 0 && tag.value.length > 0) {
        res += "\n";
      }
      let i = 0;
      const size = tag.value.length;
      for (const item of tag.value) {
        if (indent > 0) res += " ".repeat(level * indent + indent);
        res += _stringify(item, indent, level + 1);
        ++i;
        if (i < size) res += ",";
        if (indent > 0) res += "\n";
      }
      if (indent > 0 && tag.value.length > 0) res += " ".repeat(level * indent);
      res += "]";
      return res;
    }
    if (tag.kind === "array") {
      let res = "";

      if (
        tag.type === "string" ||
        tag.type === "map" ||
        tag.type === "list" ||
        tag.type === "array"
      ) {
        res = "[";
        if (indent > 0 && tag.value.length > 0) {
          res += "\n";
        }
        let i = 0,
          size = tag.value.length;
        for (const item of tag.value) {
          if (indent > 0) res += " ".repeat(level * indent + indent);
          res += _stringify(item, indent, level + 1);
          ++i;
          if (i < size) res += ",";
          if (indent > 0) res += "\n";
        }
        if (indent > 0 && tag.value.length > 0)
          res += " ".repeat(level * indent);
      } else {
        let bt = tag.type[0];
        if (tag.type === "long" || tag.type === "double") bt = bt.toUpperCase();
        res = "[" + bt + ";";

        let i = 0,
          size = tag.value.length;
        for (const item of tag.value) {
          if (i > 20) {
            res += "...";
            break;
          }
          res += item;
          ++i;
          if (i < size) res += "," + " ";
        }
      }

      res += "]";
      return res;
    }
  }

  return _stringify(tag, indent, 0);
}

if (import.meta.main) {
  console.log(
    stringify(read(gunzip(Deno.readFileSync(Deno.args[0]))), 2, true)
  );
}
