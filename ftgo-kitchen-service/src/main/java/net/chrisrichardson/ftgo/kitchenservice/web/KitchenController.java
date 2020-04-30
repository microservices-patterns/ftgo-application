package net.chrisrichardson.ftgo.kitchenservice.web;

import net.chrisrichardson.ftgo.kitchenservice.api.web.TicketAcceptance;
import net.chrisrichardson.ftgo.kitchenservice.domain.KitchenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class KitchenController {

  private KitchenService kitchenService;

  public KitchenController(KitchenService kitchenService) {
    this.kitchenService = kitchenService;
  }

  @RequestMapping(path="/tickets/{ticketId}/accept", method= RequestMethod.POST)
  public void acceptTicket(@PathVariable long ticketId, @RequestBody TicketAcceptance ticketAcceptance) {
    kitchenService.accept(ticketId, ticketAcceptance.getReadyBy());
  }
}
