from flask import Flask, request, jsonify
import requests
import os
import base64

app = Flask(__name__)

# Load configuration from environment variables
FINERACT_BASE_URL = os.getenv("FINERACT_BASE_URL", "https://sandbox.mifos.community/fineract-provider/api/v1")
FINERACT_USERNAME = os.getenv("FINERACT_USERNAME", "mifos")
FINERACT_PASSWORD = os.getenv("FINERACT_PASSWORD", "password")
FINERACT_TENANT_ID = os.getenv("FINERACT_TENANT_ID", "default")

# Create a basic auth header
credentials = f"{FINERACT_USERNAME}:{FINERACT_PASSWORD}"
basic_auth = f"Basic {base64.b64encode(credentials.encode()).decode()}"

headers = {
    "Content-Type": "application/json",
    "Fineract-Platform-TenantId": FINERACT_TENANT_ID,
    "Authorization": basic_auth,
}

@app.route('/clients', methods=['GET'])
def list_clients():
    offset = request.args.get('offset', 0)
    limit = request.args.get('limit', 20)
    response = requests.get(f"{FINERACT_BASE_URL}/clients", headers=headers, params={'offset': offset, 'limit': limit})
    return jsonify(response.json())

@app.route('/clients/<int:client_id>', methods=['GET'])
def get_client(client_id):
    response = requests.get(f"{FINERACT_BASE_URL}/clients/{client_id}", headers=headers)
    return jsonify(response.json())

@app.route('/clients', methods=['POST'])
def create_client():
    client_data = request.json
    response = requests.post(f"{FINERACT_BASE_URL}/clients", headers=headers, json=client_data)
    return jsonify(response.json())

@app.route('/loans', methods=['GET'])
def list_loans():
    offset = request.args.get('offset', 0)
    limit = request.args.get('limit', 20)
    response = requests.get(f"{FINERACT_BASE_URL}/loans", headers=headers, params={'offset': offset, 'limit': limit})
    return jsonify(response.json())

@app.route('/loans/<int:loan_id>', methods=['GET'])
def get_loan(loan_id):
    response = requests.get(f"{FINERACT_BASE_URL}/loans/{loan_id}", headers=headers)
    return jsonify(response.json())

@app.route('/loans', methods=['POST'])
def create_loan():
    loan_data = request.json
    response = requests.post(f"{FINERACT_BASE_URL}/loans", headers=headers, json=loan_data)
    return jsonify(response.json())

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000,debug=True)
