# pip install pycryptodome
# pip install pycryptodomex

import base64
import hashlib
import sys
import logging

from Cryptodome.Cipher import AES
import requests
from requests.structures import CaseInsensitiveDict

DOMAIN = "https://dev-api.oneec.ai"
ORDERS_URL = "/oapi/v1/data/merchant/orders"
HASH_NAME = "SHA256"
IV_LENGTH = 12
ITERATION_COUNT = 65536
KEY_LENGTH = 32
SALT_LENGTH = 16
TAG_LENGTH = 16
PARTNER_KEY_ID = 'dv1UYp'
MERCHANT_ACCESS_TOKEN = 'QkNk+7SnB7CPbgX84Oi7awB2rUyC4VBUpTdaQs1X2pDNwG1Ll+81fEs3jLsOtWSf5AUviae/n7jfTfsjhVPYEnYFEl7uiqbSIvSChAtvTbM9j9c+HQKrj+XIDT6dR3TaXLyF7N6/HRbz7aYhFqCDLpPpdy3Td2eryg89a4hBKhTQ4AUlqTZERo25'
SECRET_KEY = "UVM2eXZMWWV0aDZwbXZlZ0JFRFFOQ3kzOXA3bWJhbDU="
SECRET_IV = "r0cAhHJA6J73ZfCg"
HASH_KEY = 'Wfcx88ABXYzKw2luWiomOD1CKJvokcZj'

logging.basicConfig(filename='oneec-python.log', level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")


# 加密
def encrypt(secret_key, secret_iv, plain_message):
    try:
        secret = base64.b64decode(secret_key.encode('utf-8'))
        print(secret)
        iv = secret_iv.encode()

        cipher = AES.new(secret, AES.MODE_GCM, iv)
        encrypted_message_byte, tag = cipher.encrypt_and_digest(plain_message.encode("utf-8"))
        encoded_cipher_byte = base64.b64encode(encrypted_message_byte + tag)

        return encoded_cipher_byte.decode('utf-8')

    except Exception as e:
        logging.error(f'{__file__} : {e}')
        return e


# 解密
def decrypt(secret_key, secret_iv, cipher_message):
    try:
        decoded_cipher_byte = base64.b64decode(cipher_message.encode('utf-8'))
        tag = decoded_cipher_byte[-TAG_LENGTH:]
        encrypted_message_byte = decoded_cipher_byte[:-TAG_LENGTH]

        secret = base64.b64decode(secret_key.encode('utf-8'))
        iv = secret_iv.encode()

        cipher = AES.new(secret, AES.MODE_GCM, iv)
        decrypted_message_byte = cipher.decrypt_and_verify(encrypted_message_byte, tag)

        return decrypted_message_byte.decode('utf-8')

    except Exception as e:
        logging.error(f'{__file__} : {e}')
        return e


# X-sign
def get_x_sign(url, body, hash_key):
    return hashlib.sha256(f'{url}{body}{hash_key}'.encode('utf-8')).hexdigest()


def get_orders(limit, start, order_create_date, order_status, channel_id, channel_setting_id, ship_start_date,
               ship_end_date):
    try:
        param = f'?shipStartDate={ship_start_date}&shipEndDate={ship_end_date}&channelId={channel_id}&channelSettingId={channel_setting_id}&orderCreateDate={order_create_date}&orderStatus={order_status}&start={start}&limit={limit}'
        endpoint = DOMAIN + ORDERS_URL + param
        # encrypted_data = encrypt(SECRET_KEY, SECRET_IV, endpoint)
        print(endpoint)

        headers = CaseInsensitiveDict()
        headers['Accept'] = 'application/json'
        headers['X-sign'] = get_x_sign(ORDERS_URL, param, HASH_KEY)
        headers['Authorization'] = f'Bearer {PARTNER_KEY_ID}.{MERCHANT_ACCESS_TOKEN}'
        headers['Content-Type'] = 'application/json'

        response = requests.get(endpoint, headers=headers).json()
        print(response)

        decrypted_data = decrypt(SECRET_KEY, SECRET_IV, response.get('data'))

        return decrypted_data

    except Exception as e:
        logging.error(f'{__file__} : {e}')
        return e


outputFormat = '{:<25}:{}'
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
        '?shipStartDate=2022-03-16T16:46:05.005Z&shipEndDate=2023-03-16T16:46:05.005Z&channelId=AyNAVo&channelSettingId=partner_unit_test&start=0&limit=100',
        HASH_KEY
    )
)

print("\n------ Data ------")
print(
    get_orders(1, 0, '', '', '', '', '2022-03-16T16:46:05.005Z', '2022-03-17T16:46:05.005Z')
)
