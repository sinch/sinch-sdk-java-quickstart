package voice;

import com.sinch.sdk.domains.voice.VoiceService;

public class VoiceQuickStart {

    private final VoiceService voiceService;

    public VoiceQuickStart(VoiceService voiceService) {
        this.voiceService = voiceService;

        // replace by your code and business logic
        Snippet.execute(this.voiceService);
    }
}
