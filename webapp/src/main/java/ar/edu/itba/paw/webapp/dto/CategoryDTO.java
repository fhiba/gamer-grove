package ar.edu.itba.paw.webapp.dto;

public class CategoryDTO {
    private String category;

    public static CategoryDTO fromCategory(String category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategory(category);
        return categoryDTO;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
