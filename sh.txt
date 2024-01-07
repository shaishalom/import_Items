import glob
import os
import shutil


def copy_all_files(source_folder:str,outfilename: str):

    file_types = ('*.java', '*.txt', '*.xml','*.py' , '*.yaml','*.csv', '*.yml','*.js','*.bat','*.sh')
    files_grabbed = []

    with open(outfilename, 'wb') as outfile:

        for file_type in file_types:
            path = os.path.join(source_folder, "**" , file_type)
            for filename in glob.glob(path, recursive=True ):
                if filename == outfilename:
                    # don't want to copy the output into the output
                    continue
                with open(filename, 'rb') as readfile:
                    print(f"copy {filename} to {outfilename}")
                    msg = "-" * len(filename)
                    outfile.write(bytes("\n"+filename + "\n", 'utf-8'))
                    outfile.write(bytes(msg +"\n" , 'utf-8'))
                    shutil.copyfileobj(readfile, outfile)


from cryptography.fernet import Fernet
import base64, hashlib

def gen_fernet_key(passcode:bytes) -> bytes:
    assert isinstance(passcode, bytes)
    hlib = hashlib.md5()
    hlib.update(passcode)
    return base64.urlsafe_b64encode(hlib.hexdigest().encode('latin-1'))

def encript_file(file_input:str,file_output:str,key):
    with open(file_input, 'r') as file:
        data = file.read().replace('\n', '')


    #key = Fernet.generate_key()
    fernet = Fernet(key)
    data_encrypt = fernet.encrypt(data.encode())

    with open(file_output, 'wb') as outfile:
        outfile.write(data_encrypt)
    print (key)
    return data_encrypt
def decript_file(file_input: str,key, file_output:str):
    with open(file_input, 'r') as file:
        data = file.read().replace('\n', '')

    fernet = Fernet(key)
    text = fernet.decrypt(data).decode()
    print(text)
    with open(file_output, 'wb') as outfile:
        outfile.write(bytes(str(text),'utf-8'))

    return text

def merge_and_encrypt(name, source_folder, target_folder,key):
    merge_file = fr"{target_folder}\{name}.txt"
    copy_all_files(fr"{source_folder}" ,merge_file)
    merge_file_enc = fr"{target_folder}\{name}_e.txt"
    encript_file(merge_file, merge_file_enc,key)

def decrypt(name, target_folder,key):
    merge_file_enc = fr"{target_folder}\{name}_e.txt"
    decrypt_file = fr"{target_folder}\{name}_d.txt"
    result= decript_file(merge_file_enc,key,decrypt_file)
    print(f"store file in {decrypt_file}")
    print(f"{result=}")


if __name__ == '__main__':
    #merge_file=r"c:\temp\1.txt"
    #merge_file_enc = r"c:\temp\11.txt"
    #output_file = r"c:\temp\111.txt"
    #copy_all_files(r"c:\workspace" ,merge_file)
    passcode = 'guycarmel'
    key = gen_fernet_key(passcode.encode('utf-8'))
    #encript_file(merge_file, merge_file_enc,key)

    #passcode1 = 'guycarmel'
    #key1 = gen_fernet_key(passcode1.encode('utf-8'))
    source_folder=r"c:\workspace"
    target_folder = r"c:\temp"
    merge_and_encrypt("1",source_folder,target_folder,key)

    decrypt("1",target_folder,key)


