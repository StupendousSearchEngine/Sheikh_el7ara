import os

def delete_files_with_pattern(directory, prefix, suffix):
    for filename in os.listdir(directory):
        if filename.startswith(prefix) and filename.endswith(suffix):
            filepath = os.path.join(directory, filename)
            try:
                os.remove(filepath)
                print(f"Deleted file: {filepath}")
            except Exception as e:
                print(f"Error deleting file: {filepath}")
                print(e)

# Directory where the files are located
directory = r'E:\CUFE\CMP02\Second Term\APT\Project\Sheikh_el7ara'

# Prefix and suffix to match files
prefix = 'Download'
suffix = '.html'
print("hellllo")
delete_files_with_pattern(directory, prefix, suffix)