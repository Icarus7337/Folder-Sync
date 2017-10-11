# Folder-Sync

A small code to sync folders across different devices. The devices should be connected over a common network and both should be accessible.

The complete paths for the folders to be synced need to be provided as an input to the code.

The function does the following:

-> If either folder is empty, it is duly filled with data from the other folder.

-> If same named subfolders/files are encountered, the data within them is synced by calling the sync function recursively.

-> If any different file or subfolder is encountered, it is copied into the required folder.

After the completion of the the sync operation the 'last modified' fields of both folders are set same to show that they are in sync.
