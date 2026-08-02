package com.bloodnetwork.app.system;

import android.content.Context;

import com.bloodnetwork.app.controller.EmergencyController;
import com.bloodnetwork.app.graph.Graph;
import com.bloodnetwork.app.model.BloodBank;
import com.bloodnetwork.app.model.Donor;
import com.bloodnetwork.app.model.Hospital;
import com.bloodnetwork.app.service.AuthService;
import com.bloodnetwork.app.service.BloodBankService;
import com.bloodnetwork.app.service.DonorMatchingService;
import com.bloodnetwork.app.service.DonorResponseService;
import com.bloodnetwork.app.service.DonorService;
import com.bloodnetwork.app.service.EmergencyRequestService;
import com.bloodnetwork.app.service.HospitalService;
import com.bloodnetwork.app.service.NotificationService;
import com.bloodnetwork.app.service.RequestQueueService;
import com.bloodnetwork.app.service.StockTransactionService;
import com.bloodnetwork.app.util.DataLoader;

import java.io.IOException;
import java.util.List;

public class EmergencyBloodSystem {

    private static EmergencyBloodSystem instance;
    private EmergencyController controller;
    private AuthService authService;

    private EmergencyBloodSystem(Context context) throws IOException {
        DataLoader loader = new DataLoader(context.getApplicationContext());

        Graph graph = loader.buildGraph();

        HospitalService hospitalService = new HospitalService();
        for (Hospital h : loader.loadHospitals()) hospitalService.addHospital(h);

        BloodBankService bloodBankService = new BloodBankService(context.getApplicationContext());
        for (BloodBank bb : loader.loadBloodBanks()) bloodBankService.addBloodBank(bb);
        bloodBankService.loadPersistedUpdates();

        DonorService donorService = new DonorService(context.getApplicationContext());
        for (Donor d : loader.loadDonors()) donorService.addDonor(d);
        donorService.loadPersistedUpdates();

        DonorMatchingService matchingService = new DonorMatchingService(donorService, graph);
        RequestQueueService queueService = new RequestQueueService(context.getApplicationContext());
        DonorResponseService responseService = new DonorResponseService(queueService);
        NotificationService notificationService = new NotificationService(context.getApplicationContext());
        StockTransactionService transactionService = new StockTransactionService(context.getApplicationContext());
        EmergencyRequestService emergencyRequestService = new EmergencyRequestService(context.getApplicationContext());

        this.authService = new AuthService(context.getApplicationContext());

        this.controller = new EmergencyController(
                hospitalService, bloodBankService, donorService,
                matchingService, queueService, responseService,
                graph, notificationService, transactionService,
                emergencyRequestService);
    }

    public static synchronized EmergencyBloodSystem getInstance(Context context) throws IOException {
        if (instance == null) {
            instance = new EmergencyBloodSystem(context);
        }
        return instance;
    }

    public EmergencyController getController() {
        return controller;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
