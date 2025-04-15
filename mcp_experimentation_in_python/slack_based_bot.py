
import asyncio
import os
from mcp_agent.app import MCPApp
from mcp_agent.agents.agent import Agent
from mcp_agent.workflows.llm.augmented_llm_openai import OpenAIAugmentedLLM

app = MCPApp(name="mcp_slack_agent")
os.environ['OPENAI_API_KEY']=""

async def example_usage():
    async with app.run() as agent_app:
        logger = agent_app.logger
        context = agent_app.context

        slack_agent = Agent(
            name="slack_finder",
            instruction="""You are an agent with access to the filesystem, 
            as well as the ability to look up Slack conversations. Your job is to identify 
            the closest match to a user's request, make the appropriate tool calls, 
            and return the results.""",

            server_names=["filesystem", "slack"],
        )

        context.config.mcp.servers["filesystem"].args.extend([os.getcwd()])

        async with slack_agent:
            logger.info("slack: Connected to server, calling list_tools...")
            result = await slack_agent.list_tools()
            logger.info("Tools available:", data=result.model_dump())

            llm = await slack_agent.attach_llm(OpenAIAugmentedLLM)
            result = await llm.generate_str(
                message="can you write messages from all slack channel history to file and store it locally as message_history.txt but messages should be in proper cleaned format", #can be used to scrap mifos messages but feel free to use any queries here
            )
            logger.info(f"Result: {result}")

            # Multi-turn conversations for additional functionallity
            result = await llm.generate_str(
                message="send a ""hi everyone I am Keshav"" message to the channel", #feel free to replace with MIFOS context
            )
            logger.info(f"Result: {result}")


if __name__ == "__main__":

    asyncio.run(example_usage())
  

