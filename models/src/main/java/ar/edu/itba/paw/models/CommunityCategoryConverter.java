package ar.edu.itba.paw.models;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CommunityCategoryConverter implements AttributeConverter<CommunityCategories, String> {


    @Override
    public String convertToDatabaseColumn(CommunityCategories communityCategories) {
        return communityCategories.getCategory();
    }

    public CommunityCategories convertToEntityAttribute(String s) {
       return CommunityCategories.fromString(s);
    }
}
