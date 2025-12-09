package com.nova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.google.genai.types.Schema;

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Starting Gemini API call..." );

        String instruction = "Your name is NOVA, an advanced AI assistant developed by Nova Corp. "
                + "You are here to help users with their questions and provide information on a wide range of topics. "
                + "Be friendly, informative, and concise in your responses.";

        String rawJsonSchemaString = """
            {
            "type": "object",
            "properties": {
                "assistantResponse": {
                "type": "string",
                "description": "The AI assistant's conversational response."
                }
            },
            "required": ["assistantResponse"]
            }
            """;
            
        try {
            // The client automatically picks up the GEMINI_API_KEY environment variable.
            Client client = new Client();

            // Initialize conversation history
            List<Content> conversation = new ArrayList<>();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("\nYou (type 'exit' to quit): ");
                String userPrompt = scanner.nextLine();
                if (userPrompt == null) break;
                userPrompt = userPrompt.trim();
                if (userPrompt.isEmpty()) continue;
                if ("exit".equalsIgnoreCase(userPrompt)) {
                    System.out.println("Exiting conversation.");
                    break;
                }

                // Add user's message to conversation
                conversation.add(
                    Content.builder()
                        .role("user")
                        .parts(Collections.singletonList(Part.builder().text(userPrompt).build()))
                        .build()
                );

                
                GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash-lite",
                    conversation,
                    GenerateContentConfig.builder()
                        .systemInstruction(
                            Content.builder()
                                .role("user")
                                .parts(Collections.singletonList(Part.builder().text(instruction).build()))
                                .build()
                        )
                        .temperature(1.0f)
                        .responseMimeType("application/json")
                        .responseSchema(Schema.fromJson(rawJsonSchemaString))
                        .build()
                );

                // --- Parse the JSON response into our Java object ---
                String jsonResponse = response.text();
                //AssistantResponse parsedResponse = gson.fromJson(jsonResponse, AssistantResponse.class);
                //String assistantText = parsedResponse.getAssistantResponse();
                String assistantText = jsonResponse; // Placeholder until JSON parsing is implemented
                System.out.println("Model: \n" + assistantText);

                conversation.add(
                    Content.builder()
                        .role("model")
                        .parts(Collections.singletonList(Part.builder().text(assistantText).build()))
                        .build()
                );
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
