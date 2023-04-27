export interface VoiceBiometricsResponse {
    success: boolean;
    error: {
        code: number;
        description: string;
    };
    id: string;
    cpf: string;
    external_id: string;
    created_at: string;
    result: {
        recommended_action: string;
        reasons: string[];
    };
    details: {
        flag: {
            id: string;
            type: string;
            description: string;
            status: string;
        };
        voice_match: {
            result: string;
            confidence: number;
            status: string;
        };
        antispoofing: {
            result: string;
            status: string;
        };
    };
}

