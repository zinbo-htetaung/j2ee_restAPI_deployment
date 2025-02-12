package SparklePro.sparkle_ws.dbaccess;

import java.sql.Timestamp;

public class BookingDetail {
    private int bookingDetailId;
    private int bookingId;
    private int serviceId;
    private String customerReq;
    private Timestamp bookingDate;
    private String address;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String serviceName;

    // Getters and Setters
    public int getBookingDetailId() { return bookingDetailId; }
    public void setBookingDetailId(int bookingDetailId) { this.bookingDetailId = bookingDetailId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getCustomerReq() { return customerReq; }
    public void setCustomerReq(String customerReq) { this.customerReq = customerReq; }

    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}

