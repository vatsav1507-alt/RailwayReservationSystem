

package com.railway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.railway.model.Passenger;
import com.railway.model.Ticket;
import com.railway.model.Train;
import com.railway.util.JsonUtil;

public class ReservationManager {

    private final List<Train> trains = new ArrayList<>();
    private final List<Ticket> tickets = new ArrayList<>();

    private int nextTicketId = 1;

    private static final String TRAIN_FILE = "trains.json";
    private static final String TICKET_FILE = "tickets.json";

    public ReservationManager() {
        loadData();
    }

    // Load saved trains and tickets
    private void loadData() {
        Train[] savedTrains =
                JsonUtil.load(TRAIN_FILE, Train[].class);

        if (savedTrains != null) {
            trains.addAll(Arrays.asList(savedTrains));
        }

        Ticket[] savedTickets =
                JsonUtil.load(TICKET_FILE, Ticket[].class);

        if (savedTickets != null) {
            tickets.addAll(Arrays.asList(savedTickets));

            for (Ticket ticket : tickets) {
                if (ticket.getTicketId() >= nextTicketId) {
                    nextTicketId = ticket.getTicketId() + 1;
                }
            }
        }

        System.out.println("Saved railway data loaded.");
    }

    // Save trains and tickets
    private void saveData() {
        JsonUtil.save(trains.toArray(new Train[0]), TRAIN_FILE);
        JsonUtil.save(tickets.toArray(new Ticket[0]), TICKET_FILE);
    }

    public void addTrain(Train train) {
        if (train == null || findTrain(train.getTrainNumber()) != null) {
            System.out.println("Invalid or duplicate train.");
            return;
        }

        trains.add(train);
        saveData();
        System.out.println("Train added successfully.");
    }

    public void displayTrains() {
        if (trains.isEmpty()) {
            System.out.println("No trains available.");
            return;
        }

        for (Train train : trains) {
            System.out.println(train);
        }
    }

    public Train findTrain(int trainNumber) {
        for (Train train : trains) {
            if (train.getTrainNumber() == trainNumber) {
                return train;
            }
        }
        return null;
    }

    public Ticket bookTicket(
            Passenger passenger,
            int trainNumber,
            int seatsRequested,
            double farePerSeat) {

        Train train = findTrain(trainNumber);

        if (passenger == null || train == null) {
            System.out.println("Passenger or train not found.");
            return null;
        }

        if (passenger.getPassengerId() <= 0
                || passenger.getPassengerName() == null
                || passenger.getPassengerName().isBlank()
                || seatsRequested <= 0
                || farePerSeat < 0
                || !Double.isFinite(farePerSeat)) {
            System.out.println("Invalid booking details.");
            return null;
        }

        if (train.getAvailableSeats() < seatsRequested) {
            System.out.println("Not enough seats available.");
            return null;
        }

        train.setAvailableSeats(
                train.getAvailableSeats() - seatsRequested);

        double totalFare = seatsRequested * farePerSeat;

        Ticket ticket = new Ticket(
                nextTicketId++,
                passenger,
                train,
                seatsRequested,
                totalFare);

        tickets.add(ticket);
        saveData();

        System.out.println("Ticket booked successfully!");
        System.out.println(ticket);

        return ticket;
    }

    public boolean cancelTicket(int ticketId) {
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            if (ticket.getTicketId() == ticketId) {
                ticket.getTrain().setAvailableSeats(
                        ticket.getTrain().getAvailableSeats()
                                + ticket.getSeatsBooked());

                tickets.remove(i);
                saveData();

                System.out.println("Ticket cancelled successfully.");
                return true;
            }
        }

        System.out.println("Ticket not found.");
        return false;
    }

    public void displayTickets() {
        if (tickets.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }
}