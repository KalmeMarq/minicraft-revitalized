class BinaryReader {
    private _data: Uint8Array;
    private _view: DataView;
    private _cursor: number;

    constructor(data: Uint8Array) {
        this._cursor = 0;
        this._view = new DataView(data.buffer);
        this.data = data;
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
}