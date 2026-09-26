package com.alooa.leja.model;

import com.alooa.leja.exception.InvalidTradeException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    @Column(precision = 19, scale = 4)
    private BigDecimal positionSize;

    @NotNull(message = "Contract Size is required")
    @Positive(message = "Contract Size must be greater than zero")
    @Column(precision = 19, scale = 4)
    private BigDecimal contractSize;

    @NotNull(message = "Entry price is required")
    @Column(precision = 19, scale = 8)
    private BigDecimal entryPrice;

    @NotNull(message = "Exit price is required")
    @Column(precision = 19, scale = 8)
    private BigDecimal exitPrice;

    @NotNull(message = "Stop loss price is required")
    @Positive
    @Column(precision = 19, scale = 8)
    private BigDecimal stopLoss;

    @NotNull(message = "Take profit price is required")
    @Positive
    @Column(precision = 19, scale = 8)
    private BigDecimal takeProfit;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Session is required")
    private Session session;

    private String reason;

    private Instant executedAt = Instant.now();

    protected Trade() {} // hibernate needs this to fill an empty object in before postgres rows has any values

    public Trade(String symbol, Direction direction, Session session, BigDecimal positionSize,
                 BigDecimal contractSize, BigDecimal entryPrice, BigDecimal exitPrice, BigDecimal stopLoss, BigDecimal takeProfit, String reason, Instant executedAt) {
        this.symbol = sanitizeSymbol(symbol);
        this.direction = direction;
        this.positionSize = positionSize;
        this.contractSize = contractSize;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.session = session;
        this.stopLoss = stopLoss;
        this.takeProfit = takeProfit;
        this.reason = reason;
        this.executedAt = executedAt != null ? executedAt : Instant.now();
    }

    private static String sanitizeSymbol(String symbol) {
        return symbol != null ? symbol.trim().toUpperCase() : null;
    }

    public Long getId() {return this.id;}

    public String getSymbol(){
        return this.symbol;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public BigDecimal getPositionSize() {
        return this.positionSize;
    }

    public BigDecimal getPnL() {
        if (getExitPrice() == null) return BigDecimal.ZERO;

        int direction = (getDirection() == Direction.BUY) ? 1 : -1;

        BigDecimal pnl = getExitPrice().subtract(getEntryPrice())
                .multiply(getPositionSize())
                .multiply(getContractSize())
                .multiply(BigDecimal.valueOf(direction));

        return pnl.setScale(2, RoundingMode.HALF_UP);
    }

    public String getReason() {
        return reason;
    }

    public Session getSession() {
        return session;
    }

    public void checkPriceLevels() {
        List<String> errors = new ArrayList<>();
        boolean buy = direction == Direction.BUY;

        if (buy ? stopLoss.compareTo(entryPrice) >= 0 : stopLoss.compareTo(entryPrice) <= 0) {
            errors.add(buy
                    ? "Stop loss must be below the entry for a buy"
                    : "Stop loss must be above the entry for a sell");
        }

        if (buy ? takeProfit.compareTo(entryPrice) <= 0 : takeProfit.compareTo(entryPrice) >= 0) {
            errors.add(buy
                    ? "Take profit must be above the entry for a buy"
                    : "Take profit must be below the entry for a sell");
        }

        if (!errors.isEmpty()) {
            throw new InvalidTradeException(errors);
        }
    }

    public String getOutcome() {
        if (this.getPnL().compareTo(BigDecimal.ZERO) > 0) return "WIN";
        if (this.getPnL().compareTo(BigDecimal.ZERO) < 0) return "LOSS";
        return "BREAK EVEN";
    }
    public BigDecimal getContractSize() {
        return contractSize;
    }

    public BigDecimal getRiskToRewardRatio() {
        if (getStopLoss() == null || getTakeProfit() == null) {
            return null; // Cannot calculate R:R without both targets
        }

        BigDecimal risk = getEntryPrice().subtract(getStopLoss()).abs();
        BigDecimal reward = getTakeProfit().subtract(getEntryPrice()).abs();

        if (risk.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // Prevent division by zero
        }

        return reward.divide(risk, 2, RoundingMode.HALF_UP); // e.g., 2.50 for a 1:2.5 R:R
    }

    public void setContractSize(BigDecimal contractSize) {
        this.contractSize = contractSize;
    }

    public BigDecimal getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(BigDecimal entryPrice) {
        this.entryPrice = entryPrice;
    }

    public BigDecimal getExitPrice() {
        return exitPrice;
    }

    public void setExitPrice(BigDecimal exitPrice) {
        this.exitPrice = exitPrice;
    }

    public BigDecimal getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(BigDecimal takeProfit) {
        this.takeProfit = takeProfit;
    }

    public BigDecimal getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(BigDecimal stopLoss) {
        this.stopLoss = stopLoss;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSymbol(String symbol) {
        this.symbol = sanitizeSymbol(symbol);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPositionSize(BigDecimal positionSize) {
        this.positionSize = positionSize;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
