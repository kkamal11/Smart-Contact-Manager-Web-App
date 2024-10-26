package com.kamal.scm_app.utils.alert_message;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    private String message;
    @Builder.Default
    private MessageType type = MessageType.blue;
}
