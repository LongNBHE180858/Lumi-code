/** Keep optional numeric form inputs null instead of converting an empty string to zero. */
export const optionalNumber = (value: string | number | null | undefined): number | null =>
  value == null || (typeof value === 'string' && value.trim() === '') ? null : Number(value);

/** The API stores an ordered array; the CRUD editor uses one objective per line. */
export const objectivesFromText = (value: string | string[] | null | undefined): string[] | null =>
  value == null ? null : (Array.isArray(value) ? value : value.split(/\r?\n/)).map(item => item.trim()).filter(Boolean);

export const objectivesToText = (value: string[] | null | undefined): string => value?.join('\n') ?? '';
