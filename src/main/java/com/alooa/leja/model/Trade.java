package com.alooa.leja.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "trades") // making this trade model map to "trades" table to be made in leja db
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id automatically starts at 1 and auto increments per trade
    private Long id;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Direction is required")
    private Direction direction;

    @NotNull(message = "Position Size is required")
    @Positive(message = "Position must be greater than zero")
    private Double positionSize;

    @NotNull(message = "PnL is required")
    private Double pnl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Session is required")
    private Session session;

    private String reason;

    protected Trade() {} // hibernate needs this to fill an empty object in before postgres rows has any values

    public Trade(String symbol, Direction direction, Session session, Double positionSize,
                 Double pnl, String reason){
        this.symbol = symbol != null ? symbol.trim().toUpperCase() : null;
        this.direction = direction;
        this.positionSize = positionSize;
        this.session = session;
        this.reason = reason;
        this.pnl = pnl;
    }

    //getters
    public Long getId() {return this.id;}

    public String getSymbol(){
        return this.symbol;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public Double getPositionSize() {
        return this.positionSize;
    }

    public Double getPnl() {
        return this.pnl;
    }

    public String getReason() {
        return reason;
    }

    public Session getSession() {
        return session;
    }

    public String getOutcome() {
        if (this.pnl == null) return "PENDING";
        if (this.pnl > 0) return "WIN";
        if (this.pnl < 0) return "LOSS";
        return "BREAK EVEN";
    }

    //setters

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPositionSize(Double positionSize) {
        this.positionSize = positionSize;
    }

    public void setPnl(Double pnl) {
        this.pnl = pnl;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
