#!/usr/bin/env python3

import json
import os
import urllib.request

def split_version(version):
    return version.split('-')

def get_master_version(url):
    with urllib.request.urlopen(url) as response:
        data = json.loads(response.read().decode())
    return data['master']['version']

def read_prev_version():
    prev_file_path = ".prev.txt"
    with open(prev_file_path, 'r') as file:
        prev_version = file.read().strip()
    return split_version(prev_version)

def update_zig_spec(new_version):
    spec_file_path = "zig.spec"
    prev_version = read_prev_version()
    print(f"Previous version: {prev_version}")
    print(f"New version: {new_version}")

    prev_first = prev_version[0] if len(prev_version) > 0 else ''
    prev_second = prev_version[1] if len(prev_version) > 1 else '1'
    new_first = new_version[0] if len(new_version) > 0 else ''
    new_second = new_version[1] if len(new_version) > 1 else '1'

    if prev_first != new_first or prev_second != new_second:
        with open(spec_file_path, 'r') as file:
            content = file.read()
        updated_content = content.replace(f"{prev_first}", f"{new_first}")
        if prev_second is not None and new_second is not None:
            updated_content = updated_content.replace(f"%define prerelease {prev_second}", f"%define prerelease {new_second}")
            with open(spec_file_path, 'w') as file:
                file.write(updated_content)

def write_prev_version(new_version):
    prev_file_path = ".prev.txt"
    with open(prev_file_path, 'w') as file:
        file.write('-'.join(new_version) + '\n')

if __name__ == "__main__":
    url = "https://ziglang.org/download/index.json"
    new_version = split_version(get_master_version(url))
    update_zig_spec(new_version)
    print(f"Updated zig.spec with version: {new_version}")
    write_prev_version(new_version)
