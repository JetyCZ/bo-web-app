package cz.upce.webapp.dao.stock.repository;

import cz.upce.webapp.dao.stock.model.DbVersion;
import cz.upce.webapp.dao.stock.model.Item;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;
import java.util.List;
import java.util.Optional;

@Repository
public interface DbVersionRepository extends JpaRepository<DbVersion, Integer>
{
}
