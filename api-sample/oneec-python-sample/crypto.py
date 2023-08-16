# pip install pycryptodome
# pip install pycryptodomex

import base64
import hashlib

from Cryptodome.Cipher import AES
from Cryptodome.Random import get_random_bytes

HASH_NAME = "SHA256"
IV_LENGTH = 12
ITERATION_COUNT = 65536
KEY_LENGTH = 32
SALT_LENGTH = 16
TAG_LENGTH = 16
SECRET_KEY = "UVM2eXZMWWV0aDZwbXZlZ0JFRFFOQ3kzOXA3bWJhbDU="
SECRET_IV = "r0cAhHJA6J73ZfCg"
HASH_KEY = 'Wfcx88ABXYzKw2luWiomOD1CKJvokcZj'

# 加密
def encrypt(secret_key, secret_iv, plain_message):
    secret = base64.b64decode(secret_key.encode('utf-8'))
    print(secret)
    iv = SECRET_IV.encode()
    
    cipher = AES.new(secret, AES.MODE_GCM, iv)
    encrypted_message_byte, tag = cipher.encrypt_and_digest(plain_message.encode("utf-8"))
    encoded_cipher_byte = base64.b64encode(encrypted_message_byte + tag)
    
    return encoded_cipher_byte.decode('utf-8')

# 解密
def decrypt(secret_key, secret_iv, cipher_message):
    decoded_cipher_byte = base64.b64decode(cipher_message.encode('utf-8'))
    tag = decoded_cipher_byte[-TAG_LENGTH:]
    encrypted_message_byte = decoded_cipher_byte[:-TAG_LENGTH]

    secret = base64.b64decode(secret_key.encode('utf-8'))
    iv = SECRET_IV.encode()
    
    cipher = AES.new(secret, AES.MODE_GCM, iv)
    decrypted_message_byte = cipher.decrypt_and_verify(encrypted_message_byte, tag)
    
    return decrypted_message_byte.decode('utf-8')

# X-sign
def get_x_sign(url, body, hash_key):
    return hashlib.sha256(f'{url}{body}{hash_key}'.encode('utf-8')).hexdigest()

outputFormat = "{:<25}:{}"
plain_text = '{"total":0,"totalPage":1,"data":[]}'

print("------ AES-GCM Encryption ------")
cipher_text = encrypt(SECRET_KEY, SECRET_IV, plain_text)
print(outputFormat.format("encryption input", plain_text))
print(outputFormat.format("encryption output", cipher_text))

decrypted_text = decrypt(SECRET_KEY, SECRET_IV, cipher_text)

print("\n------ AES-GCM Decryption ------")
print(outputFormat.format("decryption input", cipher_text))
print(outputFormat.format("decryption output", decrypted_text))

print("\n------ X-sign ------")
print(
    get_x_sign(
        '/oapi/v1/data/merchant/orders', 
        '?shipStartDate=2022-03-16T16:46:05.005Z&shipEndDate=2023-03-16T16:46:05.005Z&channelId=AyNAVo&channelSettingId=partner_unit_test&start=0&limit=2',
        HASH_KEY
    )
)
