# Asset-Cache-Packer
### An application tool to create and edit caches which contains assets

I've written this application tool to create and edit .cache files
It was written to combine game assets into 1 cache file
which can be retrieved during runtime.

[DOWNLOAD PREBUILT JAR](https://github.com/DTanJP/Asset-Cache-Packer/raw/master/Download/AssetPacker.jar)

I tried to keep this as basic and simple as possible.
This is just a way for indie game developers to:
1. Manage and organize their assets
2. A way of protecting their assets from being used without permission*


*This is kind of a double edge sword here. If an game developer uses this and you also happen to have this tool, you can rip out their assets.
However in order to block against that, you just have to download a copy of this and modify the source code to encrypt the bytes data upon saving onto the cache or create some new pattern that create a different kind of cache file making my application tool unable to parse it correctly.*

*I strongly encourage you to download this and build it yourself instead of going for the prebuilt jar. This way you can implement encryption or a different cache model*

Instructions
1. Start off by pressing "Create cache" or "Load cache"
![The main view of the application](https://raw.githubusercontent.com/DTanJP/Asset-Cache-Packer/master/images/Screenshot_1.png)

2. Afterwards you can being to add files, but you must remember to pack the cache afterwards inorder to update the cache file
Simply adding files only adds it to memory. By packing it, you overwrite the cache file with the current files in memory.
The same applies for removing files.
![Viewing a cache](https://raw.githubusercontent.com/DTanJP/Asset-Cache-Packer/master/images/Screenshot_2.png)

3. Double clicking the names of the files on the right lets you read them, unless if they're an image (jpg, png, gif), then they will be rendered onto the left panel

# ! NOTICE !
Don't bother to import the cache source code from this application onto your projects as it's integrated with the application and you will end up doing some refactoring. Check my other repository for the standalone cache class

* I will continue to add small tweaks and improvements onto this tool, please let me know of any bugs that you have found and I will do my best to fix it and post a patch
