package am.aua.cs322.orgchart.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class JsonError implements Serializable {
    @NonNull
    private String message;
}
