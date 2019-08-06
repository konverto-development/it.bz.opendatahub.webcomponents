package it.bz.opendatahub.webcomponents.dataservice.controller;

import it.bz.opendatahub.webcomponents.common.data.rest.WebcomponentEntry;
import it.bz.opendatahub.webcomponents.dataservice.converter.impl.WebcomponentEntryConverter;
import it.bz.opendatahub.webcomponents.dataservice.data.dto.WebcomponentDto;
import it.bz.opendatahub.webcomponents.dataservice.data.model.WebcomponentModel;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import it.bz.opendatahub.webcomponents.dataservice.repository.WebcomponentRepository;
import it.bz.opendatahub.webcomponents.dataservice.service.WebcomponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/webcomponent")
public class WebcomponentController {
    private WebcomponentService webcomponentService;
    private WebcomponentEntryConverter webcomponentEntryConverter;

    @Autowired
    public WebcomponentController(WebcomponentService webcomponentService,
                                  WebcomponentEntryConverter webcomponentEntryConverter) {

        this.webcomponentService = webcomponentService;
        this.webcomponentEntryConverter = webcomponentEntryConverter;
    }

    @GetMapping
    public ResponseEntity<Page<WebcomponentEntry>> find(
                                                           @RequestParam(name = "tags", required = false) String[] tags,
                                                           @RequestParam(name = "term", required = false, defaultValue = "") String term,
                                                           Pageable pageRequest
                                                           ) {

        List<String> tagList = Collections.emptyList();
        if(tags != null) {
            tagList = Arrays.asList(tags);
        }

        Page<WebcomponentDto> resultPage = webcomponentService.listAll(pageRequest, tagList, term);

        return new ResponseEntity<>(new PageImpl(webcomponentEntryConverter.dtoToRest(resultPage.getContent()), pageRequest, resultPage.getTotalElements()), HttpStatus.OK);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<WebcomponentEntry>> listLatest() {
        return null;
    }

    @GetMapping("/popular")
    public ResponseEntity<List<WebcomponentEntry>> listPopular() {
        return null;
    }

    @GetMapping("/detail/{uuid}")
    public ResponseEntity<WebcomponentEntry> getOne(@PathVariable String uuid) {
        return null;
    }
}
