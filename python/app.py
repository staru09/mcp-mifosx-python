import os
import base64
import requests
from flask import request, jsonify
from mcp.server.fastmcp import FastMCP

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
    """Create a new loan in Fineract."""
    response = requests.post(f"{FINERACT_BASE_URL}/loans", headers=headers, json=loan_data)
    return response.json()

if __name__ == '__main__':

    mcp.run()
