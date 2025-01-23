package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentCreationDTO {

    @NotBlank(message = "{NotBlank}")
    @Size(max = 500, message = "{MaxLength}")
    private String body;

    public CommentCreationDTO() {
    }

    public CommentCreationDTO(String body, Integer postId) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
