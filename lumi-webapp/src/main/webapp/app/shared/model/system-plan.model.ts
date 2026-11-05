import dayjs from 'dayjs';

export interface ISystemPlan {
  id?: number;
  name?: string;
  price?: number;
  durationDays?: number;
  storageQuotaMb?: number;
  aiGenerationLimit?: number;
  maxClasses?: number;
  planType?: string;
  targetRole?: string;
  status?: string;
  aiLimitPeriod?: string;
  features?: string;
  updatedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<ISystemPlan> = {};
