package cz.upce.webapp.dao.stock.model;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Tomas Kodym
 */

@Entity
public class DbVersion
{
    @Column
    @Id
    public Integer appliedUpdateId;

    public DbVersion() {
    }

    public DbVersion(Integer appliedUpdateId) {
        this.appliedUpdateId = appliedUpdateId;
    }
}