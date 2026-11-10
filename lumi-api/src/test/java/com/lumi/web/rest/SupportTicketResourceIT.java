package com.lumi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.IntegrationTest;
import com.lumi.domain.SupportTicket;
import com.lumi.domain.TicketMessage;
import com.lumi.domain.User;
import com.lumi.repository.SupportTicketRepository;
import com.lumi.repository.TicketMessageRepository;
import com.lumi.service.dto.TicketMessageContent;
import com.lumi.service.storage.CourseObjectStorage;
import com.lumi.service.storage.R2CourseObjectStorage;
import jakarta.persistence.EntityManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
class SupportTicketResourceIT {

    private static final String API = "/api/support-tickets";
    private static final String ADMIN_API = "/api/admin/support-tickets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SupportTicketRepository ticketRepository;

    @Autowired
    private TicketMessageRepository messageRepository;

    @MockitoBean
    private R2CourseObjectStorage imageStorage;

    @Test
    @Transactional
    void anonymousCannotCreate() throws Exception {
        mockMvc
            .perform(
                post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\":\"GENERAL\",\"subject\":\"Help\",\"content\":\"Question\"}")
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void userRoleAloneCanCreateWithServerOwnedFields() throws Exception {
        User owner = persistUser("ticketowner");
        mockMvc
            .perform(
                post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\":\"GENERAL\",\"subject\":\"Need help\",\"content\":\"First message\"}")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("OPEN"))
            .andExpect(jsonPath("$.userId").value(owner.getId()))
            .andExpect(jsonPath("$.userLogin").value("ticketowner"));

        SupportTicket ticket = ticketRepository
            .findByUserId(owner.getId(), org.springframework.data.domain.PageRequest.of(0, 10))
            .getContent()
            .getFirst();
        assertThat(ticket.getCreatedAt()).isNotNull();
        assertThat(ticket.getUpdatedAt()).isEqualTo(ticket.getCreatedAt());
        TicketMessage message = messageRepository
            .findByTicketIdOrderByCreatedAtAscIdAsc(ticket.getId(), org.springframework.data.domain.PageRequest.of(0, 10))
            .getContent()
            .getFirst();
        assertThat(message.getSender().getId()).isEqualTo(owner.getId());
        assertThat(message.getContent()).isEqualTo("First message");
        assertThat(message.getCreatedAt()).isNotNull();
        assertThat(message.getCreatedAt()).isEqualTo(ticket.getUpdatedAt());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_PARENT")
    void businessRoleWithoutUserRoleCanCreate() throws Exception {
        persistUser("ticketowner");
        mockMvc
            .perform(
                post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\":\"GENERAL\",\"subject\":\"Need help\",\"content\":\"Question\"}")
            )
            .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void createsTicketWithImageInFirstMessage() throws Exception {
        User owner = persistUser("ticketowner");
        when(imageStorage.uploadTicketImage(any(), any(), any(), any())).thenAnswer(invocation ->
            new TicketMessageContent.Image("tickets/" + invocation.getArgument(0) + "/a.png", "issue.png")
        );

        mockMvc
            .perform(
                multipart(API)
                    .file(new MockMultipartFile("images", "issue.png", "image/png", pngBytes()))
                    .param("type", "GENERAL")
                    .param("subject", "Need help")
                    .param("content", "First message")
            )
            .andExpect(status().isCreated());

        SupportTicket ticket = ticketRepository
            .findByUserId(owner.getId(), org.springframework.data.domain.PageRequest.of(0, 10))
            .getContent()
            .getFirst();
        mockMvc
            .perform(get(API + "/{id}/messages", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].content").value("First message"))
            .andExpect(jsonPath("$[0].images.length()").value(1));
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void ownerCanReadOwnTicketAndMessages() throws Exception {
        User owner = persistUser("ticketowner");
        SupportTicket ticket = persistTicket(owner, "OPEN");
        persistMessage(ticket, owner, "First message");

        mockMvc.perform(get(API)).andExpect(status().isOk()).andExpect(header().exists("X-Total-Count"));
        mockMvc
            .perform(get(API + "/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userLogin").value("ticketowner"));
        mockMvc
            .perform(get(API + "/{id}/messages", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].content").value("First message"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketoutsider", authorities = "ROLE_USER")
    void otherUserCannotReadOrMessageTicket() throws Exception {
        User owner = persistUser("ticketowner");
        persistUser("ticketoutsider");
        SupportTicket ticket = persistTicket(owner, "RESOLVED");

        mockMvc.perform(get(API)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get(API + "/{id}", ticket.getId())).andExpect(status().isForbidden());
        mockMvc.perform(get(API + "/{id}/messages", ticket.getId())).andExpect(status().isForbidden());
        mockMvc.perform(get(API + "/{id}/messages/1/images/0/access", ticket.getId())).andExpect(status().isForbidden());
        mockMvc
            .perform(
                post(API + "/{id}/messages", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"Intrusion\"}")
            )
            .andExpect(status().isForbidden());
        mockMvc.perform(post(API + "/{id}/reopen", ticket.getId())).andExpect(status().isForbidden());
        mockMvc.perform(get(ADMIN_API)).andExpect(status().isForbidden());
        mockMvc
            .perform(
                patch(ADMIN_API + "/{id}/status", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"IN_PROGRESS\"}")
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void ownerMessageReopensResolvedTicket() throws Exception {
        User owner = persistUser("ticketowner");
        SupportTicket ticket = persistTicket(owner, "RESOLVED");

        mockMvc
            .perform(
                post(API + "/{id}/messages", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"Still need help\"}")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.senderLogin").value("ticketowner"));
        entityManager.flush();
        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(ticket.getUpdatedAt()).isAfter(ticket.getCreatedAt());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void ownerCanReopenResolvedTicket() throws Exception {
        User owner = persistUser("ticketowner");
        SupportTicket ticket = persistTicket(owner, "RESOLVED");

        mockMvc
            .perform(post(API + "/{id}/reopen", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        mockMvc.perform(post(API + "/{id}/reopen", ticket.getId())).andExpect(status().isConflict());
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketadmin", authorities = "ROLE_ADMIN")
    void adminCanFindReplyResolveAndReopenTicket() throws Exception {
        User owner = persistUser("ticketowner");
        persistUser("ticketadmin");
        SupportTicket ticket = persistTicket(owner, "OPEN");

        mockMvc
            .perform(get(ADMIN_API).param("status", "OPEN").param("search", "Need help"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ticket.getId()));
        mockMvc.perform(get(API + "/{id}", ticket.getId())).andExpect(status().isOk());
        mockMvc
            .perform(
                post(API + "/{id}/messages", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"Working on it\"}")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.senderLogin").value("ticketadmin"));
        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");

        mockMvc
            .perform(
                patch(ADMIN_API + "/{id}/status", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"status\":\"RESOLVED\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RESOLVED"));
        mockMvc
            .perform(post(API + "/{id}/reopen", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketadmin", authorities = "ROLE_ADMIN")
    void adminMessageReopensResolvedTicket() throws Exception {
        User owner = persistUser("ticketowner");
        persistUser("ticketadmin");
        SupportTicket ticket = persistTicket(owner, "RESOLVED");

        mockMvc
            .perform(
                post(API + "/{id}/messages", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"Follow up\"}")
            )
            .andExpect(status().isCreated());
        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void messageWithTwoImagesCanBeReadByOwner() throws Exception {
        User owner = persistUser("ticketowner");
        SupportTicket ticket = persistTicket(owner, "RESOLVED");
        when(imageStorage.uploadTicketImage(any(), any(), any(), any())).thenAnswer(invocation -> {
            String fileName = invocation.getArgument(2);
            return new TicketMessageContent.Image("tickets/" + ticket.getId() + "/" + (fileName.equals("first.png") ? "a" : "b") + ".png", fileName);
        });
        when(imageStorage.ticketImageAccess(any(), any(), any())).thenReturn(new CourseObjectStorage.Access("https://example.test/image", Instant.now().plusSeconds(300)));
        byte[] png = pngBytes();

        mockMvc
            .perform(
                multipart(API + "/{id}/messages", ticket.getId())
                    .file(new MockMultipartFile("images", "first.png", "image/png", png))
                    .file(new MockMultipartFile("images", "second.png", "image/png", png))
                    .param("content", "Ảnh lỗi")
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Ảnh lỗi"))
            .andExpect(jsonPath("$.images.length()").value(2));

        entityManager.flush();
        assertThat(ticket.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(ticket.getUpdatedAt()).isAfter(ticket.getCreatedAt());
        TicketMessage message = messageRepository
            .findByTicketIdOrderByCreatedAtAscIdAsc(ticket.getId(), org.springframework.data.domain.PageRequest.of(0, 10))
            .getContent()
            .getFirst();
        mockMvc
            .perform(get(API + "/{id}/messages/{messageId}/images/0/access", ticket.getId(), message.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").value("https://example.test/image"));
    }

    @Test
    @Transactional
    @WithMockUser(username = "ticketowner", authorities = "ROLE_USER")
    void inputValidationAndLegacyCrudAreRejected() throws Exception {
        User owner = persistUser("ticketowner");
        SupportTicket ticket = persistTicket(owner, "OPEN");

        mockMvc
            .perform(
                post(API).contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"  \",\"subject\":\"Help\",\"content\":\"Question\"}")
            )
            .andExpect(status().isBadRequest());
        mockMvc
            .perform(
                post(API + "/{id}/messages", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":\"  \"}")
            )
            .andExpect(status().isBadRequest());
        mockMvc.perform(delete(API + "/{id}", ticket.getId())).andExpect(status().isMethodNotAllowed());
        mockMvc
            .perform(
                put(API + "/{id}", ticket.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
            )
            .andExpect(status().isMethodNotAllowed());
        mockMvc
            .perform(post("/api/ticket-messages").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isNotFound());
    }

    private User persistUser(String login) {
        User user = UserResourceIT.createEntity();
        user.setLogin(login);
        user.setEmail(login + "@localhost");
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private SupportTicket persistTicket(User owner, String status) {
        Instant createdAt = Instant.now().minusSeconds(60);
        SupportTicket ticket = new SupportTicket()
            .type("GENERAL")
            .subject("Need help")
            .status(status)
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .user(owner);
        entityManager.persist(ticket);
        entityManager.flush();
        return ticket;
    }

    private void persistMessage(SupportTicket ticket, User sender, String content) {
        TicketMessage message = new TicketMessage().content(content).createdAt(Instant.now()).sender(sender).ticket(ticket);
        entityManager.persist(message);
        entityManager.flush();
    }

    private byte[] pngBytes() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), "png", output);
        return output.toByteArray();
    }
}
