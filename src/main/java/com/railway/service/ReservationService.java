
package com.railway.service;

import com.railway.model.Passenger;

import com.railway.model.Ticket;
import com.railway.model.Train;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReservationService {

    private final List<Ticket> tickets = new ArrayList<>();
    private int nextTicketId = 1;

    // Book a ticket
    public synchronized Ticket bookTicket(
            Train train,
            Passenger passenger,
            int seatsBooked,
            double fare) {

        if (train == null || passenger == null) {
            System.out.println("Train and passenger are required.");
            return null;
        }

        if (seatsBooked <= 0 || fare < 0) {
            System.out.println("Invalid number of seats or fare.");
            return null;
        }

        Ticket ticket = new Ticket(
                nextTicketId++,
                passenger,
                train,
                seatsBooked,
                fare
        );

        tickets.add(ticket);

        System.out.println("Ticket booked successfully!");
        System.out.println(ticket);

        return ticket;
    }

    // Display all tickets
    public synchronized void viewAllTickets() {

        if (tickets.isEmpty()) {
            System.out.println("No tickets available.");
            return;
        }

        System.out.println("\n===== ALL TICKETS =====");

        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }

    // Cancel a ticket using its integer ID
    public synchronized void cancelTicket(int ticketId) {

        Iterator<Ticket> iterator = tickets.iterator();

        while (iterator.hasNext()) {

            Ticket ticket = iterator.next();

            if (ticket.getTicketId() == ticketId) {
                iterator.remove();

                System.out.println(
                        "Ticket " + ticketId + " cancelled successfully."
                );
                return;
            }
        }

        System.out.println("Ticket not found: " + ticketId);
    }

    // Get the number of active tickets
    public synchronized int getTicketCount() {
        return tickets.size();
    }
}