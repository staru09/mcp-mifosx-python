import os
import base64
import requests
from flask import request, jsonify
from mcp.server.fastmcp import FastMCP
from typing import Optional, Dict, Any

# Initialize the MCP server
mcp = FastMCP("Fineract MCP Server")

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
@mcp.tool()
def list_clients(offset: int = 0, limit: int = 20):
    """Retrieve a list of clients from Fineract."""
    response = requests.get(f"{FINERACT_BASE_URL}/clients", headers=headers, params={'offset': offset, 'limit': limit})
    return response.json()

@mcp.tool()
def get_client(client_id: int):
    """Retrieve details of a specific client."""
    response = requests.get(f"{FINERACT_BASE_URL}/clients/{client_id}", headers=headers)
    return response.json()

@mcp.tool()
def create_client(client_data: dict):
    """Create a new client in Fineract."""
    response = requests.post(f"{FINERACT_BASE_URL}/clients", headers=headers, json=client_data)
    return response.json()

@mcp.tool()
def search_clients(query: str, limit: int = 10, exact_match: bool = False):
    """Search for clients by name, ID, or other attributes."""
    params = {
        'query': query,
        'resource': 'clients',
        'exactMatch': str(exact_match).lower(),
        'limit': limit
    }
    response = requests.get(f"{FINERACT_BASE_URL}/search", headers=headers, params=params)
    return response.json()

@mcp.tool()
def activate_client(client_id: int, activation_data: dict):
    """Activate a pending client."""
    response = requests.post(f"{FINERACT_BASE_URL}/clients/{client_id}?command=activate", headers=headers, json=activation_data)
    return response.json()

@mcp.tool()
def list_loans(offset: int = 0, limit: int = 20):
    """Retrieve a list of loans from Fineract."""
    response = requests.get(f"{FINERACT_BASE_URL}/loans", headers=headers, params={'offset': offset, 'limit': limit})
    return response.json()

@mcp.tool()
def get_loan(loan_id: int):
    """Retrieve details of a specific loan."""
    response = requests.get(f"{FINERACT_BASE_URL}/loans/{loan_id}", headers=headers)
    return response.json()

@mcp.tool()
def create_loan(loan_data: dict):
    """Create a new loan application in Fineract."""
    response = requests.post(f"{FINERACT_BASE_URL}/loans", headers=headers, json=loan_data)
    return response.json()

@mcp.tool()
def approve_loan(loan_id: int, approval_data: dict):
    """Approve a pending loan application."""
    response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}?command=approve", headers=headers, json=approval_data)
    return response.json()

@mcp.tool()
def disburse_loan(loan_id: int, disbursement_data: dict):
    """Disburse an approved loan."""
    response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}?command=disburse", headers=headers, json=disbursement_data)
    return response.json()

@mcp.tool()
def get_loan_repayment_schedule(loan_id: int):
    """Retrieve the repayment schedule for a specific loan."""
    response = requests.get(f"{FINERACT_BASE_URL}/loans/{loan_id}/repaymentschedule", headers=headers)
    return response.json()

@mcp.tool()
def make_loan_repayment(loan_id: int, repayment_data: dict):
    """Make a repayment transaction for a specific loan."""
    response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}/transactions?command=repayment", headers=headers, json=repayment_data)
    return response.json()

@mcp.tool()
def list_loan_products():
    """Retrieve a list of available loan products."""
    response = requests.get(f"{FINERACT_BASE_URL}/loanproducts", headers=headers)
    return response.json()

@mcp.tool()
def get_loan_product(product_id: int):
    """Retrieve details of a specific loan product."""
    response = requests.get(f"{FINERACT_BASE_URL}/loanproducts/{product_id}", headers=headers)
    return response.json()

if __name__ == '__main__':

    mcp.run()

## pip install mcp[cli] uv flask
## RUN IT WITH mcp dev app.py
