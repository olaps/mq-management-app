
  export enum Direction {
    INBOUND = 'INBOUND',
    OUTBOUND = 'OUTBOUND'
  }
  
  export enum ProcessedFlowType {
    MESSAGE = 'MESSAGE',
    ALERTING = 'ALERTING',
    NOTIFICATION = 'NOTIFICATION'
  }
  
  export interface Partner {
    id?: number;
    alias: string;
    type: string;
    direction: Direction;
    application?: string;
    processedFlowType: ProcessedFlowType;
    description: string;
    createdAt?: string;
    updatedAt?: string;
  }