package timeseries.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import timeseries.model.PriceListLifecycleAggregate;
import timeseries.model.PriceListLifecycleEvent;
import timeseries.service.PriceListLifecycleService;

import java.util.List;

@RestController
@RequestMapping("/timeseries")
public class PriceListLifecycleController {

    private final PriceListLifecycleService priceListLifecycleService;

    public PriceListLifecycleController(PriceListLifecycleService priceListLifecycleService) {
        this.priceListLifecycleService = priceListLifecycleService;
    }

    @GetMapping
    public ResponseEntity<List<PriceListLifecycleEvent>> findAll() {
        return new ResponseEntity<>(priceListLifecycleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/team")
    public ResponseEntity<List<PriceListLifecycleEvent>> findAllByTeamId(@RequestParam String teamId) {
        return new ResponseEntity<>(priceListLifecycleService.findAllByTeamId(teamId), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<PriceListLifecycleEvent>> findAllByUserId(@RequestParam String userId) {
        return new ResponseEntity<>(priceListLifecycleService.findAllByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/analytics/draft-duration-by-team")
    public ResponseEntity<List<PriceListLifecycleAggregate>> averageDraftDurationByTeam() {
        return new ResponseEntity<>(priceListLifecycleService.averageDraftDurationByTeam(), HttpStatus.OK);
    }

    @GetMapping("/analytics/activity-by-user")
    public ResponseEntity<List<PriceListLifecycleAggregate>> activityByUserBetween(@RequestParam String userId, @RequestParam String start, @RequestParam String stop) {
        return new ResponseEntity<>(priceListLifecycleService.activityByUserBetween(userId, start, stop), HttpStatus.OK);
    }

    @GetMapping("/analytics/slowest-in-review")
    public ResponseEntity<List<PriceListLifecycleAggregate>> slowestPriceListsAboveAverageReviewTime() {
        return new ResponseEntity<>(priceListLifecycleService.slowestPriceListsAboveAverageReviewTime(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Boolean> save(@RequestBody PriceListLifecycleEvent event) {
        return priceListLifecycleService.save(event)
                ? new ResponseEntity<>(true, HttpStatus.CREATED)
                : new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestParam String priceListId) {
        return priceListLifecycleService.deleteRecord(priceListId)
                ? new ResponseEntity<>(true, HttpStatus.OK)
                : new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/seed")
    public ResponseEntity<Integer> seed(@RequestParam(defaultValue = "2000") int count) {
        return new ResponseEntity<>(priceListLifecycleService.seed(count), HttpStatus.CREATED);
    }
}