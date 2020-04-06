package cz.upce.webapp.dao.stock.model;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import javax.persistence.*;

/**
 * @author Tomáš Kodym
 */

@Entity(name = "Suppliers")
@Table(name = "suppliers")
@Transactional
public class Supplier implements Serializable
{


    @Id
    @Column(insertable=true, updatable=true, unique=true, nullable=false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "free_transport", nullable = true, length = 50)
    private Integer freeTransport;

    @Column(nullable = true, length = 100)
    private String email;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = StoredFile.class, fetch = FetchType.LAZY)
    private StoredFile pricelist;

    @Column
    private String pricelistName;

    @Column
    /**
     * Fixed cost of transport
     */
    public Double transportCost = 0D;

    @Column
    public Date lastImport;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public StoredFile getPricelist() {
        return pricelist;
    }

    public void setPricelist(StoredFile pricelist) {
        this.pricelist = pricelist;
    }

    public Integer getFreeTransport() {
        return freeTransport;
    }

    public void setFreeTransport(Integer freeTransport) {
        this.freeTransport = freeTransport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPricelistName(String pricelistName) {
        this.pricelistName = pricelistName;
    }

    public String getPricelistName() {
        return pricelistName;
    }
}
