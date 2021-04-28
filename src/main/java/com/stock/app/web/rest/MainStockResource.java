package com.stock.app.web.rest;

import com.stock.app.domain.MainStock;
import com.stock.app.repository.MainStockRepository;
import com.stock.app.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.stock.app.domain.MainStock}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MainStockResource {

    private final Logger log = LoggerFactory.getLogger(MainStockResource.class);

    private static final String ENTITY_NAME = "stockMainStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MainStockRepository mainStockRepository;

    public MainStockResource(MainStockRepository mainStockRepository) {
        this.mainStockRepository = mainStockRepository;
    }

    /**
     * {@code POST  /main-stocks} : Create a new mainStock.
     *
     * @param mainStock the mainStock to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mainStock, or with status {@code 400 (Bad Request)} if the mainStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/main-stocks")
    public ResponseEntity<MainStock> createMainStock(@RequestBody MainStock mainStock) throws URISyntaxException {
        log.debug("REST request to save MainStock : {}", mainStock);
        if (mainStock.getId() != null) {
            throw new BadRequestAlertException("A new mainStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MainStock result = mainStockRepository.save(mainStock);
        return ResponseEntity.created(new URI("/api/main-stocks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /main-stocks} : Updates an existing mainStock.
     *
     * @param mainStock the mainStock to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mainStock,
     * or with status {@code 400 (Bad Request)} if the mainStock is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mainStock couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/main-stocks")
    public ResponseEntity<MainStock> updateMainStock(@RequestBody MainStock mainStock) throws URISyntaxException {
        log.debug("REST request to update MainStock : {}", mainStock);
        if (mainStock.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MainStock result = mainStockRepository.save(mainStock);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mainStock.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /main-stocks} : get all the mainStocks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mainStocks in body.
     */
    @GetMapping("/main-stocks")
    public ResponseEntity<List<MainStock>> getAllMainStocks(Pageable pageable) {
        log.debug("REST request to get a page of MainStocks");
        Page<MainStock> page = mainStockRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /main-stocks/:id} : get the "id" mainStock.
     *
     * @param id the id of the mainStock to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mainStock, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/main-stocks/{id}")
    public ResponseEntity<MainStock> getMainStock(@PathVariable Long id) {
        log.debug("REST request to get MainStock : {}", id);
        Optional<MainStock> mainStock = mainStockRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(mainStock);
    }

    /**
     * {@code DELETE  /main-stocks/:id} : delete the "id" mainStock.
     *
     * @param id the id of the mainStock to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/main-stocks/{id}")
    public ResponseEntity<Void> deleteMainStock(@PathVariable Long id) {
        log.debug("REST request to delete MainStock : {}", id);
        mainStockRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
