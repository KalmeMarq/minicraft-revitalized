from pathlib import Path
import platform
import requests
import os
import subprocess

match platform.system():
    case 'Windows':
        lwjgl_natives = 'natives-windows' 
    case 'Linux':
        lwjgl_natives = 'natives-linux'
    case 'Darwin':
        lwjgl_natives = 'natives-macos'
    case _:
        lwjgl_natives = "unknown"

libraries = [
    f"org.lwjgl:lwjgl:3.3.3|c={lwjgl_natives}",
    f"org.lwjgl:lwjgl-glfw:3.3.3|c={lwjgl_natives}",
    f"org.lwjgl:lwjgl-opengl:3.3.3|c={lwjgl_natives}",
    f"org.lwjgl:lwjgl-stb:3.3.3|c={lwjgl_natives}",
    f"org.lwjgl:lwjgl-jemalloc:3.3.3|c={lwjgl_natives}",
    "org.joml:joml:1.10.5",
    "org.jetbrains:annotations:24.1.0",
    "org.apache.logging.log4j:log4j-api:2.23.1",
    "org.apache.logging.log4j:log4j-core:2.23.1",
    "com.fasterxml.jackson.core:jackson-databind:2.17.1",
    "com.fasterxml.jackson.core:jackson-annotations:2.17.1",
    "com.fasterxml.jackson.core:jackson-core:2.17.1",
    "org.yaml:snakeyaml:2.2",
    "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1",
    "com.github.KalmeMarq:arg-option-parser:e8a468fef5",
    "io.netty:netty-buffer:4.1.110.Final",
    "io.netty:netty-codec:4.1.110.Final",
    "io.netty:netty-common:4.1.110.Final",
    "io.netty:netty-handler:4.1.110.Final",
    "io.netty:netty-resolver:4.1.110.Final",
    "io.netty:netty-transport:4.1.110.Final"
]


def decompose_library(library: str):
    parts = library.split(":")
    classifier = None
    if parts[2].count("|c=") == 1:
        classifier = parts[2].split("|c=")[1]
        parts[2] = parts[2].split("|c=")[0]

    return {'group': parts[0], 'name': parts[1], 'version': parts[2], 'classifier': classifier}


cp = []


def main():
    libpath = Path(__file__).parent / "libraries"
    libpath.mkdir(exist_ok=True)

    for lib in libraries:
        decomp_lib = decompose_library(lib)
        lib_dir_local_path = f"{decomp_lib['group'].replace(".", "/")}/{decomp_lib['name']}/{decomp_lib['version']}"
        repo_url = "https://search.maven.org/remotecontent?filepath="
        if lib.startswith("com.github"):
            repo_url = "https://jitpack.io/"

        lib_url = f"{repo_url}{lib_dir_local_path}/{decomp_lib['name']}-{decomp_lib['version']}.jar"

        if lib.startswith("com.github"):
            print(lib_url)

        lib_dir = libpath / lib_dir_local_path
        lib_dir.mkdir(exist_ok=True, parents=True)

        lib_jar_path = lib_dir / f"{decomp_lib['name']}-{decomp_lib['version']}.jar"

        cp.append(lib_jar_path.__str__())

        if not lib_jar_path.exists():
            resp = requests.get(lib_url)
            with open(lib_jar_path, "wb") as f:
                f.write(resp.content)

        if decomp_lib['classifier'] is not None:
            lib_classifier_url = f"https://search.maven.org/remotecontent?filepath={lib_dir_local_path}/{decomp_lib['name']}-{decomp_lib['version']}-{decomp_lib['classifier']}.jar"
            lib_classifier_jar_path = lib_dir / f"{decomp_lib['name']}-{decomp_lib['version']}-{decomp_lib['classifier']}.jar"
            
            cp.append(lib_classifier_jar_path.__str__())

            if not lib_classifier_jar_path.exists():
                resp = requests.get(lib_classifier_url)
                with open(lib_classifier_jar_path, "wb") as f:
                    f.write(resp.content)

    os.chdir(Path(__file__).parent.parent / "run")
    subprocess.run(["java", "-Xmx64m", "-cp", f"{";".join(cp)};../build/libs/minicraft-revitalized-1.0.0.jar", "me.kalmemarq.minicraft.client.Main", "--savedir", "."])


if __name__ == '__main__':
    main()   
