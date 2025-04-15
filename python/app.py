import os
import base64
import sys
import time
import json
import requests
from datetime import datetime
from flask import request, jsonify,Response, stream_with_context
from mcp.server.fastmcp import FastMCP
from typing import Optional, Dict, Any, Generator

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

# Helper function for API requests
def fineract_request(method: str, endpoint: str, **kwargs) -> Dict:
    """Make a request to the Fineract API with error handling."""
    url = f"{FINERACT_BASE_URL}{endpoint}"
    try:
        response = requests.request(method, url, headers=headers, **kwargs)
        response.raise_for_status()
        return {"success": True, "data": response.json()}
    except requests.RequestException as e:
        return {
            "success": False,
            "error": str(e),
            "status_code": getattr(e.response, 'status_code', None)
        }

# SSE Implementation
def generate_client_events() -> Generator[str, None, None]:
    """Generate SSE events for client updates."""
    while True:
        try:
            # Get recent clients
            response = fineract_request("GET", "/clients", params={'orderBy': 'id', 'sortOrder': 'DESC', 'limit': 5})
            
            if response["success"]:
                event_data = {
                    "timestamp": datetime.now().isoformat(),
                    "clients": response["data"]
                }
                yield f"data: {json.dumps(event_data)}\n\n"
            else:
                yield f"data: {json.dumps({'error': 'Failed to fetch clients'})}\n\n"
            
            time.sleep(5)  # Check every 5 seconds
        except Exception as e:
            yield f"data: {json.dumps({'error': str(e)})}\n\n"
            time.sleep(5)

# MCP tools with SSE support
@mcp.tool()
def stream_client_updates():
    """Stream client updates using SSE."""
    return Response(
        stream_with_context(generate_client_events()),
        mimetype='text/event-stream'
    )


@mcp.tool()
def list_clients(offset: int = 0, limit: int = 20):
    """Retrieve a list of clients from Fineract."""
    #response = requests.get(f"{FINERACT_BASE_URL}/clients", headers=headers, params={'offset': offset, 'limit': limit})
    return fineract_request("GET", "/clients", params={'offset': offset, 'limit': limit})

@mcp.tool()
def get_client(client_id: int):
    """Retrieve details of a specific client."""
    #response = requests.get(f"{FINERACT_BASE_URL}/clients/{client_id}", headers=headers)
    return fineract_request("GET", f"/clients/{client_id}")

@mcp.tool()
def create_client(client_data: dict):
    """Create a new client in Fineract."""
    #response = requests.post(f"{FINERACT_BASE_URL}/clients", headers=headers, json=client_data)
    return fineract_request("POST", "/clients", json=client_data)

@mcp.tool()
def search_clients(query: str, limit: int = 10, exact_match: bool = False):
    """Search for clients by name, ID, or other attributes."""
    params = {
        'query': query,
        'resource': 'clients',
        'exactMatch': str(exact_match).lower(),
        'limit': limit
    }
    #response = requests.get(f"{FINERACT_BASE_URL}/search", headers=headers, params=params)
    return fineract_request("GET", "/search", params=params)

@mcp.tool()
def activate_client(client_id: int, activation_data: dict):
    """Activate a pending client."""
    #response = requests.post(f"{FINERACT_BASE_URL}/clients/{client_id}?command=activate", headers=headers, json=activation_data)
    return fineract_request("POST", f"/clients/{client_id}?command=activate", json=activation_data)


@mcp.tool()
def list_loans(offset: int = 0, limit: int = 20):
    """Retrieve a list of loans from Fineract."""
    #response = requests.get(f"{FINERACT_BASE_URL}/loans", headers=headers, params={'offset': offset, 'limit': limit})
    return fineract_request("GET", "/loans", params={'offset': offset, 'limit': limit})

@mcp.tool()
def get_loan(loan_id: int):
    """Retrieve details of a specific loan."""
    #response = requests.get(f"{FINERACT_BASE_URL}/loans/{loan_id}", headers=headers)
    return fineract_request("GET", f"/loans/{loan_id}")

@mcp.tool()
def create_loan(loan_data: dict):
    """Create a new loan application in Fineract."""
    #response = requests.post(f"{FINERACT_BASE_URL}/loans", headers=headers, json=loan_data)
    return fineract_request("POST", "/loans", json=loan_data)

@mcp.tool()
def approve_loan(loan_id: int, approval_data: dict):
    """Approve a pending loan application."""
    #response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}?command=approve", headers=headers, json=approval_data)
    return fineract_request("POST", f"/loans/{loan_id}?command=approve", json=approval_data)

@mcp.tool()
def disburse_loan(loan_id: int, disbursement_data: dict):
    """Disburse an approved loan."""
    #response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}?command=disburse", headers=headers, json=disbursement_data)
    return fineract_request("POST", f"/loans/{loan_id}?command=disburse", json=disbursement_data)


@mcp.tool()
def get_loan_repayment_schedule(loan_id: int):
    """Retrieve the repayment schedule for a specific loan."""
    #response = requests.get(f"{FINERACT_BASE_URL}/loans/{loan_id}/repaymentschedule", headers=headers)
    return fineract_request("GET", f"/loans/{loan_id}/repaymentschedule")


@mcp.tool()
def make_loan_repayment(loan_id: int, repayment_data: dict):
    """Make a repayment transaction for a specific loan."""
    #response = requests.post(f"{FINERACT_BASE_URL}/loans/{loan_id}/transactions?command=repayment", headers=headers, json=repayment_data)
    return fineract_request("POST", f"/loans/{loan_id}/transactions?command=repayment", json=repayment_data)

@mcp.tool()
def list_loan_products():
    """Retrieve a list of available loan products."""
    response = requests.get(f"{FINERACT_BASE_URL}/loanproducts", headers=headers)
    return fineract_request("GET", "/loanproducts")

@mcp.tool()
def get_loan_product(product_id: int):
    """Retrieve details of a specific loan product."""
    #response = requests.get(f"{FINERACT_BASE_URL}/loanproducts/{product_id}", headers=headers)
    return fineract_request("GET", f"/loanproducts/{product_id}")

if __name__ == '__main__':

    mcp.run()

## pip install mcp[cli] uv flask
## RUN IT WITH mcp dev app.py
