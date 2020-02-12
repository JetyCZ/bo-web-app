package cz.upce.webapp.dao.stock.repository;

import cz.upce.webapp.dao.stock.model.DbVersion;
import cz.upce.webapp.dao.stock.model.Supplier;
import cz.upce.webapp.dao.stock.repository.SupplierRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Component
public class DbUpdater implements InitializingBean {
    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    DbVersionRepository dbVersionRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("======== Starting DB UPDATE ===========");
        update(1, () -> {
            /*
            Karel Rohrer
            MKM pack s.r.o.
            Cezavy 627
            664 56 Blučina
            Mobil    :  +  420 773 697 171
            Tel.        :  +  420 530 333 270
            E mail    :    karel@rohrer.cz
            rohrer@mkmpack.cz
            www.mkmpack.cz
            www.iplody.cz */
            Supplier s = new Supplier();
            s.setId(6);
            s.setName("MKM Pack");
            s.setEmail("rohrer@mkmpack.cz");
            // If less then 45kg
            s.transportCost = 139.15;
            supplierRepository.save(s);
        });
    }

    private void update(int updateId, Runnable runnable) {
        boolean present = dbVersionRepository.findById(updateId).isPresent();
        System.out.println("Checking for existence of db version:" + updateId + ", "+present);
        if (!present) {
            runnable.run();
            dbVersionRepository.save(new DbVersion(updateId));
        }
    }
}
