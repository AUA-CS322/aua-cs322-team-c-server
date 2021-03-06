package am.aua.cs322.orgchart.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class JsonError implements Serializable {
    @NonNull
    private String message;
}
