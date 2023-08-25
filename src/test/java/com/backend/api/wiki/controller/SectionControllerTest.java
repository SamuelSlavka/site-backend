package com.backend.api.wiki.controller;

import com.backend.api.utils.Utils;
import com.backend.api.wiki.entity.Revision;
import com.backend.api.wiki.entity.Section;
import com.backend.api.wiki.model.RevisionCreationDto;
import com.backend.api.wiki.model.SectionDto;
import com.backend.api.wiki.model.SectionPaginationDto;
import com.backend.api.wiki.repository.ArticleRepository;
import com.backend.api.wiki.repository.CategoryRepository;
import com.backend.api.wiki.repository.SectionRepository;
import com.backend.api.wiki.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionController.class)
class SectionControllerTest {
    final String sectionId = "some-uid";
    final String userId = "some-uid";
    @MockBean
    SecurityContext securityContext;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private SectionService sectionService;
    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private Jwt jwt;
    private SectionDto sectionDto;
    private RevisionCreationDto revisionCreationDto;
    private Revision revision;
    private SectionPaginationDto page;

    @BeforeEach
    void setUp() {
        page = new SectionPaginationDto(0, 10, 10, 0);
        revisionCreationDto = new RevisionCreationDto("title", "text");
        revision = new Revision(revisionCreationDto);
        Section section = Section.builder().id(sectionId).latestRevision(revision).revisions(List.of(revision)).build();
        section.create(userId);

        sectionDto = section.getDto();
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @DisplayName("Return section based on user Id")
    void getUserSection() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(sectionService.getSection(sectionId, userId, page)).thenReturn(List.of(sectionDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sections/id/" + sectionId + "?page=0&offset=10&limit=10")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((revision.getTitle())));
    }

    @Test
    @DisplayName("Return section based on isPublic flag")
    void getPublicSection() throws Exception {
        when(sectionService.getPublicSection(sectionId, new SectionPaginationDto(0, 10, 10, 0))).thenReturn(List.of(sectionDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/sections/id/" + sectionId + "?page=0&pageSize=10&limit=10")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value((revision.getTitle())));
    }

    @Test
    @DisplayName("Create subsection under specified section")
    void createSubSection() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(sectionService.createSubSection(sectionId, revisionCreationDto, userId)).thenReturn(sectionDto);

        mockMvc.perform(post("/api/v1/sections/id/" + sectionId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"text\": \"text\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((sectionDto.getTitle())));
    }

    @Test
    @DisplayName("Fail creating subsection")
    void createSubSectionWithoutTitle() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(sectionService.createSubSection(eq(sectionId), any(revisionCreationDto.getClass()), eq(userId))).thenReturn(sectionDto);

        mockMvc.perform(post("/api/v1/sections/id/" + sectionId).contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"text\": \"text\"}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message not readable"));
    }

    @Test
    @DisplayName("Create revision for specified section")
    void createRevision() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));
        when(sectionService.createRevision(eq(sectionId), any(revisionCreationDto.getClass()), eq(userId))).thenReturn(sectionDto);

        mockMvc.perform(put("/api/v1/sections/id/" + sectionId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"title\", \"text\": \"text\"}")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value((sectionDto.getTitle())));
    }

    @Test
    @DisplayName("Soft delete section")
    void deleteSection() throws Exception {
        given(securityContext.getAuthentication()).willReturn(Utils.getMockJwtToken("USER", userId));

        mockMvc.perform(delete("/api/v1/sections/id/" + sectionId).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"title\", \"text\": \"text\"}")).andExpect(status().isOk());
    }

}