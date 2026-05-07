package com.pgfinder.platform.dto;

import com.pgfinder.platform.domain.RoomType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class InquiryRequest {

    @NotNull
    private Long listingId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$")
    private String phone;

    @Email
    @Size(max = 120)
    private String email;

    private LocalDate moveInDate;

    private RoomType preferredRoomType;

    @Min(1000)
    @Max(100000)
    private Integer budget;

    @Min(1)
    @Max(48)
    private Integer expectedStayMonths;

    @Size(max = 600)
    private String message;

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public RoomType getPreferredRoomType() {
        return preferredRoomType;
    }

    public void setPreferredRoomType(RoomType preferredRoomType) {
        this.preferredRoomType = preferredRoomType;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getExpectedStayMonths() {
        return expectedStayMonths;
    }

    public void setExpectedStayMonths(Integer expectedStayMonths) {
        this.expectedStayMonths = expectedStayMonths;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}