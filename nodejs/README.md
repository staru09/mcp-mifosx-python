# Apache Fineract MCP Server

    This MCP server provides access to the Apache Fineract API, allowing AI models to interact with microfinance data and operations.

    ## Features

    - Client management (search, create, view)
    - Loan management (application, approval, disbursement)
    - Resources for browsing clients and loans
    - Tools for performing specific operations

    ## Setup

    1. Clone this repository
    2. Install dependencies:
       ```
       npm install
       ```
    3. Copy the example environment file and configure it:
       ```
       cp .env.example .env
       ```
    4. Edit the `.env` file with your Fineract API credentials

    ## Usage

    ### Running the server

    ```
    npm run dev
    ```

    ### Testing with MCP Inspector

    ```
    npm run inspect
    ```

    ## Available Resources

    - `fineract://clients` - List all clients
    - `fineract://clients/{clientId}` - Get details for a specific client
    - `fineract://loans` - List all loans
    - `fineract://loans/{loanId}` - Get details for a specific loan

    ## Available Tools

    - `search_clients` - Search for clients by name or other attributes
    - `create_client` - Create a new client
    - `get_loan_details` - Get detailed information about a loan
    - `create_loan_application` - Create a new loan application
    - `approve_loan` - Approve a pending loan
    - `disburse_loan` - Disburse an approved loan

    ## Environment Variables

    - `FINERACT_BASE_URL` - Base URL for the Fineract API
    - `FINERACT_USERNAME` - Username for API authentication
    - `FINERACT_PASSWORD` - Password for API authentication
    - `FINERACT_TENANT_ID` - Tenant ID for multi-tenancy support
