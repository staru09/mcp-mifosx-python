

# Run: `pip install agno mcp groq` to install the dependencies
# Environment variables needed:
# - Create a GitHub personal access token following these steps:
#     - https://github.com/modelcontextprotocol/servers/tree/main/src/github#setup
# - export GITHUB_TOKEN: Your GitHub personal access token"

import asyncio
import os
from textwrap import dedent
from agno.agent import Agent
from agno.tools.mcp import MCPTools
from agno.models.groq import Groq
from mcp import StdioServerParameters

os.environ['GITHUB_TOKEN']=''

async def run_agent(message: str) -> None:
    """Run the GitHub agent with the given message."""

    # Initialize the MCP server
    server_params = StdioServerParameters(
        command="npx",
        args=["-y", "@modelcontextprotocol/server-github"],
      
    )

    # Create a client session to connect to the MCP server
    async with MCPTools(server_params=server_params) as mcp_tools:
        agent = Agent(
            model=Groq(id="qwen-qwq-32b",api_key=''),
            tools=[mcp_tools],
            instructions=dedent("""\
                You are a personal GitHub assistant for the user named CoderOMaster. Using your tool calling, perform input tasks assigned to you to best of your ability.

                - Use headings to organize your responses
                - Be concise and focus on relevant information\
            """),
            markdown=True,
            show_tool_calls=True,
        )

        await agent.aprint_response(message, stream=True)

# Example usage
if __name__ == "__main__":

        asyncio.run(
        run_agent(
            "Show my last merged pr"
        )
    )


