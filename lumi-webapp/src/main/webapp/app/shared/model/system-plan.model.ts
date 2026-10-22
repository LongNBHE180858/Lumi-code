export interface ISystemPlan {
  id?: number;
  name?: string;
  price?: number;
  durationDays?: number;
  storageQuotaMb?: number;
  aiGenerationLimit?: number;
  maxClasses?: number;
}

export const defaultValue: Readonly<ISystemPlan> = {};
