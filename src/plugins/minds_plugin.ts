import { registerPlugin } from '@capacitor/core';

import { VoiceBiometricsResponse } from 'src/types/voiceBiometrics';

export interface MindsPlugin {
    authentication(options: { document: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
    enrollment(options: { document: string, token: string, telephone: string }): Promise<VoiceBiometricsResponse>;
}

const Minds = registerPlugin<MindsPlugin>('Minds');

export default Minds;

