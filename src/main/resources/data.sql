-- ETF 심볼 초기 데이터 등록 (존재 시 업데이트)
INSERT INTO etf_symbol (symbol, name, market, is_active) VALUES
                                                             ('VOO', 'Vanguard S&P 500 ETF', 'US', true),
                                                             ('SPY', 'SPDR S&P 500 ETF Trust', 'US', true),
                                                             ('IVV', 'iShares Core S&P 500 ETF', 'US', true),
                                                             ('QQQ', 'Invesco QQQ Trust', 'US', true),
                                                             ('TQQQ', 'ProShares UltraPro QQQ', 'US', true),
                                                             ('VTI', 'Vanguard Total Stock Market ETF', 'US', true),
                                                             ('ARKK', 'ARK Innovation ETF', 'US', true),
                                                             ('102110', 'TIGER 200', 'KR', true),
                                                             ('069500', 'KODEX 200', 'KR', true),
                                                             ('278530', 'KODEX 200TR', 'KR', true),
                                                             ('105190', 'KINDEX 200', 'KR', true),
                                                             ('233740', 'KODEX 코스닥150 레버리지', 'KR', true),
                                                             ('122630', 'KODEX 레버리지', 'KR', true),
                                                             ('252670', 'KODEX 200선물인버스2X', 'KR', true)
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         market = VALUES(market),
                         is_active = VALUES(is_active);
