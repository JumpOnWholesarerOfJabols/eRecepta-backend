package edu.pk.jawolh.erecepta.common.user.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCodeMessage {
    private String email;
    private String code;
}
