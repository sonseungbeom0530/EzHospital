package com.example.ezhospital.Interface;

import com.example.ezhospital.Model.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();

    void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String documentId);

    void onBookingInfoLoadFailed(String message);
}
