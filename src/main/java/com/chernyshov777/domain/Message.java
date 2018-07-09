package com.chernyshov777.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
public class Message {
    /*Message timeout equals three days*/
    private final static long MESSAGE_TIMEOUT = 3 * 24 * 60 * 60 * 1000;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column
    @NotNull
    private String currency;
    @Column
    @NotNull
    private double amount;
    @Column
    @NotNull
    @JsonProperty("id")
    private String paymentId;
    @Column
    @NotNull
    @JsonProperty("external_id")
    private long externalId;
    @Column
    @NotNull
    private State status;
    @Column
    @NotNull
    @JsonProperty("sha2sig")
    private String sha2;

    @Column(nullable = false)
    @JsonIgnore
    private String contentType;

    @Column(nullable = false)
    @JsonIgnore
    private Timestamp timestamp;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Destination destination;

    protected Message() {
    }

    public Message(String contentType, Destination destination) {
        super();
        this.contentType = contentType;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.destination = destination;
    }

    public Long getDestinationId() {
        return destination.getId();
    }

    public String getDestinationUrl() {
        return destination.getUrl();
    }

    public Boolean isMessageTimeout() {
        return timestamp.getTime() < System.currentTimeMillis() - MESSAGE_TIMEOUT;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Message[id=%d, contentType='%s']", id, contentType);
    }
}
