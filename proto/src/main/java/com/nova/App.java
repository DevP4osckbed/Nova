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

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Starting Gemini API call..." );

        try {
            // The client automatically picks up the GEMINI_API_KEY environment variable.
            Client client = new Client();

            // Initialize conversation history with an optional system prompt
            List<Content> conversation = new ArrayList<>();
            conversation.add(
                Content.builder()
                    .role("model")
                    .parts(Collections.singletonList(Part.builder().text("You are a helpful assistant.").build()))
                    .build()
            );

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

                // Send the prompt / conversation to the model
                GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash", // Specify the model to use
                    conversation,
                    GenerateContentConfig.builder().build()
                );

                // Print and append the model's response
                String assistantText = response.text();
                System.out.println("Assistant: " + assistantText);

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
