export interface VoiceBiometricsResponse {
    success: boolean;
    error: {
        code: string;
        description: string;
    } | null;
    id: number;
    cpf: string;
    external_id: string;
    created_at: string;
    utc_created_at: string;
    result: {
        recommended_action: string;
        reasons: string[];
    };
    details: {
        flag: {
            type: string;
            status: string;
        } | null;
        liveness: {
            status: string;
            replay_attack: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
            deepfake: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
            sentence_match: {
                enabled: boolean;
                status: string;
                result: string;
                confidence: string;
                score: number;
                threshold: number;
            };
        };
        voice_match: {
            result: string;
            confidence: string;
            status: string;
            score: number;
            threshold: number;
        };
    };
}