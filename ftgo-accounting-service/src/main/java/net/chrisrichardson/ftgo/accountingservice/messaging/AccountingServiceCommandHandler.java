package net.chrisrichardson.ftgo.accountingservice.messaging;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import net.chrisrichardson.ftgo.accountingservice.domain.*;
import net.chrisrichardson.ftgo.accountservice.api.AccountDisabledReply;
import net.chrisrichardson.ftgo.accountservice.api.AuthorizeCommand;
import net.chrisrichardson.ftgo.accountservice.api.ReverseAuthorizationCommand;
import net.chrisrichardson.ftgo.accountservice.api.ReviseAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.sagas.eventsourcingsupport.UpdatingOptionsBuilder.replyingTo;

public class AccountingServiceCommandHandler {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AggregateRepository<Account, AccountCommand> accountRepository;

  public CommandHandlers commandHandlers() {
    return SagaCommandHandlersBuilder
            .fromChannel("accountingService")
            .onMessage(AuthorizeCommand.class, this::authorize)
            .onMessage(ReverseAuthorizationCommand.class, this::reverseAuthorization)
            .onMessage(ReviseAuthorization.class, this::reviseAuthorization)
            .build();
  }

  public void authorize(CommandMessage<AuthorizeCommand> cm) {

    AuthorizeCommand command = cm.getCommand();

    accountRepository.update(Long.toString(command.getConsumerId()),
            makeAuthorizeCommandInternal(command),
            replyingTo(cm)
                    .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                    .build());

  }

  public void reverseAuthorization(CommandMessage<ReverseAuthorizationCommand> cm) {

    ReverseAuthorizationCommand command = cm.getCommand();

    accountRepository.update(Long.toString(command.getConsumerId()),
            makeReverseAuthorizeCommandInternal(command),
            replyingTo(cm)
                    .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                    .build());

  }
  public void reviseAuthorization(CommandMessage<ReviseAuthorization> cm) {

    ReviseAuthorization command = cm.getCommand();

    accountRepository.update(Long.toString(command.getConsumerId()),
            makeReviseAuthorizeCommandInternal(command),
            replyingTo(cm)
                    .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                    .build());


  }

  private AuthorizeCommandInternal makeAuthorizeCommandInternal(AuthorizeCommand command) {
    return new AuthorizeCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderId()), command.getOrderTotal());
  }
  private ReverseAuthorizationCommandInternal makeReverseAuthorizeCommandInternal(ReverseAuthorizationCommand command) {
    return new ReverseAuthorizationCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderId()), command.getOrderTotal());
  }
  private ReviseAuthorizationCommandInternal makeReviseAuthorizeCommandInternal(ReviseAuthorization command) {
    return new ReviseAuthorizationCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderId()), command.getOrderTotal());
  }

}
