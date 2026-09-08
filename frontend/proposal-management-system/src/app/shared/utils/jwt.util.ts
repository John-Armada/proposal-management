export function decodeJwt <T = Record<string, string>> (token: string): T {
    const payload = token.split('.')[1];

    if (!payload) {
        throw new Error('Invalid JWT token');
    }

    
    try {
        const base64 = payload
        .replace(/-/g, '+')
        .replace(/_/g, '/');

        const padded = base64.padEnd(
            base64.length + ((4 - (base64.length % 4)) % 4),
            '='
        );

        const bytes = Uint8Array.from(atob(padded), char => char.charCodeAt(0));

        const json = new TextDecoder().decode(bytes);

        return JSON.parse(json) as T;
    } catch {
        return null as T;
    }
}

export function isTokenExpired(expiresAt: number): boolean {
    return expiresAt <= Date.now();
}