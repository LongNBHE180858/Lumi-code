package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.SupportTicket;
import com.lumi.domain.TicketMessage;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.SupportTicketStatus;
import com.lumi.repository.SupportTicketRepository;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.dto.SupportTicketCreateRequest;
import com.lumi.service.dto.TicketMessageCreateRequest;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SupportTicketManagementServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @Mock
    private TicketMessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SupportTicketManagementService service;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);
        owner.setLogin("ticketowner");
        authenticate("ticketowner", "ROLE_USER");
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUsesAuthenticatedOwnerAndCreatesInitialMessage() {
        when(userRepository.findOneByLogin("ticketowner")).thenReturn(Optional.of(owner));
        when(ticketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticket = invocation.getArgument(0);
            ticket.setId(21L);
            return ticket;
        });
        when(messageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(new SupportTicketCreateRequest(" GENERAL ", " Need help ", " First message "));

        ArgumentCaptor<TicketMessage> messageCaptor = ArgumentCaptor.forClass(TicketMessage.class);
        verify(messageRepository).save(messageCaptor.capture());
        assertThat(result.status()).isEqualTo("OPEN");
        assertThat(result.userId()).isEqualTo(owner.getId());
        assertThat(result.type()).isEqualTo("GENERAL");
        assertThat(messageCaptor.getValue().getSender()).isSameAs(owner);
        assertThat(messageCaptor.getValue().getTicket().getId()).isEqualTo(21L);
        assertThat(messageCaptor.getValue().getContent()).isEqualTo("First message");
    }

    @Test
    void unrelatedUserCannotReadOrMessage() {
        User outsider = new User();
        outsider.setId(11L);
        outsider.setLogin("outsider");
        authenticate("outsider", "ROLE_USER");
        when(userRepository.findOneByLogin("outsider")).thenReturn(Optional.of(outsider));
        SupportTicket ticket = ticket("RESOLVED");
        when(ticketRepository.findOneWithEagerRelationships(21L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(ticket));

        ResponseStatusException readError = assertThrows(ResponseStatusException.class, () -> service.findOne(21L));
        ResponseStatusException writeError = assertThrows(ResponseStatusException.class, () ->
            service.addMessage(21L, new TicketMessageCreateRequest("Intrusion"))
        );

        assertThat(readError.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(writeError.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void ownerMessageReopensResolvedTicket() {
        when(userRepository.findOneByLogin("ticketowner")).thenReturn(Optional.of(owner));
        SupportTicket ticket = ticket("RESOLVED");
        when(ticketRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addMessage(21L, new TicketMessageCreateRequest("Still need help"));

        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void messageNormalizesLegacyStatusWithoutChangingOtherRecords() {
        when(userRepository.findOneByLogin("ticketowner")).thenReturn(Optional.of(owner));
        SupportTicket ticket = ticket("legacy sample status");
        when(ticketRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addMessage(21L, new TicketMessageCreateRequest("Need a reply"));

        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void adminReplyStartsProcessingAnOpenTicket() {
        User admin = new User();
        admin.setId(12L);
        admin.setLogin("ticketadmin");
        authenticate("ticketadmin", "ROLE_ADMIN");
        when(userRepository.findOneByLogin("ticketadmin")).thenReturn(Optional.of(admin));
        SupportTicket ticket = ticket("OPEN");
        when(ticketRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(ticket));
        when(messageRepository.save(any(TicketMessage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addMessage(21L, new TicketMessageCreateRequest("Working on it"));

        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void onlyAdminCanResolveAndCreatorCanReopen() {
        SupportTicket ticket = ticket("IN_PROGRESS");
        ResponseStatusException denied = assertThrows(ResponseStatusException.class, () ->
            service.changeStatus(21L, SupportTicketStatus.RESOLVED)
        );
        assertThat(denied.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        authenticate("ticketadmin", "ROLE_ADMIN");
        when(ticketRepository.findByIdForUpdate(21L)).thenReturn(Optional.of(ticket));
        assertThat(service.changeStatus(21L, SupportTicketStatus.RESOLVED).status()).isEqualTo("RESOLVED");

        authenticate("ticketowner", "ROLE_USER");
        when(userRepository.findOneByLogin("ticketowner")).thenReturn(Optional.of(owner));
        assertThat(service.reopen(21L).status()).isEqualTo("IN_PROGRESS");
    }

    private SupportTicket ticket(String status) {
        return new SupportTicket().id(21L).type("GENERAL").subject("Need help").status(status).user(owner);
    }

    private void authenticate(String login, String role) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(login, "password", java.util.List.of(new SimpleGrantedAuthority(role)))
        );
    }
}
