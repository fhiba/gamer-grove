import { User } from "./User";

export interface Moderator {
  community: string;
  self: string;
  sinceDate: string;
  user: string | User;
}
